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
}
