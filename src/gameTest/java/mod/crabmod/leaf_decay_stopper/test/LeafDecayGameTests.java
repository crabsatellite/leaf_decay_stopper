package mod.crabmod.leaf_decay_stopper.test;

import java.util.ArrayList;
import java.util.List;
import mod.crabmod.leaf_decay_stopper.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("leaf_decay_stopper")
@PrefixGameTestTemplate(false)
public final class LeafDecayGameTests {
  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", timeoutTicks = 20)
  public static void enabling_removes_every_duplicate(GameTestHelper helper) throws Exception {
    var level = helper.getLevel();
    var originalConfig = new ArrayList<>(Config.DIMENSIONS_WITHOUT_DECAY.get());
    var pos = helper.absolutePos(BlockPos.ZERO.above(2));
    var originalBlock = level.getBlockState(pos);
    boolean originalDrops = level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
    var dispatcher = level.getServer().getCommands().getDispatcher();
    var source = level.getServer().createCommandSourceStack().withLevel(level);
    try {
      level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, level.getServer());
      for (int copies : List.of(1, 2, 3)) {
        var dimensions = new ArrayList<String>();
        for (int i = 0; i < copies; i++) dimensions.add("minecraft:overworld");
        dimensions.add("minecraft:the_nether");
        Config.DIMENSIONS_WITHOUT_DECAY.set(dimensions);
        var leaves = Blocks.OAK_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.DISTANCE, 7).setValue(LeavesBlock.PERSISTENT, false);
        level.setBlock(pos, leaves, 3);
        int result = dispatcher.execute("setleavesdecay minecraft:overworld true", source);
        helper.assertTrue(result > 0, "Enable command must succeed");
        helper.assertTrue(Config.isDecayEnabledForDimension(level), "Every duplicate must be removed");
        helper.assertTrue(Config.DIMENSIONS_WITHOUT_DECAY.get().equals(List.of("minecraft:the_nether")),
            "Other dimensions must be preserved");
        leaves.randomTick(level, pos, RandomSource.create(123));
        helper.assertTrue(level.getBlockState(pos).isAir(), "Unsupported leaves must decay");
        dispatcher.execute("setleavesdecay minecraft:overworld true", source);
        dispatcher.execute("setleavesdecay minecraft:overworld false", source);
        dispatcher.execute("setleavesdecay minecraft:overworld false", source);
        helper.assertTrue(Config.DIMENSIONS_WITHOUT_DECAY.get().equals(
            List.of("minecraft:the_nether", "minecraft:overworld")), "Repeated commands must be idempotent");
        helper.assertTrue(!Config.isDecayEnabledForDimension(level), "Disable must remain effective");
      }
    } finally {
      Config.DIMENSIONS_WITHOUT_DECAY.set(originalConfig);
      Config.SPEC.save();
      level.setBlock(pos, originalBlock, 3);
      level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(originalDrops, level.getServer());
    }
    helper.succeed();
  }

  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", timeoutTicks = 20)
  public static void unknown_dimension_is_rejected(GameTestHelper helper) throws Exception {
    var level = helper.getLevel();
    var original = new ArrayList<>(Config.DIMENSIONS_WITHOUT_DECAY.get());
    var source = level.getServer().createCommandSourceStack();
    boolean rejected = false;
    try {
      try {
        level.getServer().getCommands().getDispatcher().execute(
            "setleavesdecay leaf_decay_stopper:missing false", source);
      } catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) {
        rejected = true;
      }
      helper.assertTrue(rejected, "An unknown dimension must return an error");
      helper.assertTrue(Config.DIMENSIONS_WITHOUT_DECAY.get().equals(original), "A rejected command must not change configuration");
    } finally {
      Config.DIMENSIONS_WITHOUT_DECAY.set(original);
      Config.SPEC.save();
    }
    helper.succeed();
  }

  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", timeoutTicks = 20)
  public static void loaded_custom_dimension_can_be_toggled(GameTestHelper helper) throws Exception {
    var server = helper.getLevel().getServer();
    var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
        new net.minecraft.resources.ResourceLocation("leaf_decay_stopper:validation"));
    var target = server.getLevel(key);
    helper.assertTrue(target != null, "The custom test dimension must be loaded");
    var pos = new BlockPos(0, 100, 0);
    var originalBlock = target.getBlockState(pos);
    var originalConfig = new ArrayList<>(Config.DIMENSIONS_WITHOUT_DECAY.get());
    boolean originalDrops = target.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
    try {
      target.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, server);
      var source = server.createCommandSourceStack();
      var dispatcher = server.getCommands().getDispatcher();
      dispatcher.execute("setleavesdecay leaf_decay_stopper:validation false", source);
      helper.assertTrue(!Config.isDecayEnabledForDimension(target), "Custom dimension must be disabled");
      var leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 7)
          .setValue(LeavesBlock.PERSISTENT, false);
      target.setBlock(pos, leaves, 3);
      leaves.randomTick(target, pos, RandomSource.create(123));
      helper.assertTrue(target.getBlockState(pos).is(Blocks.OAK_LEAVES), "Disabled custom dimension must preserve leaves");
      dispatcher.execute("setleavesdecay leaf_decay_stopper:validation true", source);
      leaves.randomTick(target, pos, RandomSource.create(123));
      helper.assertTrue(target.getBlockState(pos).isAir(), "Enabled custom dimension must allow decay");
      helper.assertTrue(Config.isDecayEnabledForDimension(helper.getLevel()) ==
          !originalConfig.contains("minecraft:overworld"), "Another dimension must not change");
    } finally {
      Config.DIMENSIONS_WITHOUT_DECAY.set(originalConfig);
      Config.SPEC.save();
      target.setBlock(pos, originalBlock, 3);
      target.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(originalDrops, server);
    }
    helper.succeed();
  }

  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", timeoutTicks = 20)
  public static void dimension_completions_respect_prefix(GameTestHelper helper) throws Exception {
    var source = helper.getLevel().getServer().createCommandSourceStack();
    var dispatcher = helper.getLevel().getServer().getCommands().getDispatcher();
    var values = dispatcher.getCompletionSuggestions(dispatcher.parse("setleavesdecay minecraft:o", source))
        .get().getList().stream().map(s -> s.getText()).sorted().toList();
    helper.assertTrue(values.equals(List.of("minecraft:overworld")), "Completion must filter the entered dimension prefix");
    helper.succeed();
  }
}
