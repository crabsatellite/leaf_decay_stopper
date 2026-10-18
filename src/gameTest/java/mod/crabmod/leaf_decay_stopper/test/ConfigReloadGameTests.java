package mod.crabmod.leaf_decay_stopper.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import mod.crabmod.leaf_decay_stopper.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("leaf_decay_stopper")
@PrefixGameTestTemplate(false)
public final class ConfigReloadGameTests {
  @GameTest(template = "empty", batch = "leaf_config_reload", timeoutTicks = 600)
  public static void manual_saves_reload_the_live_config(GameTestHelper helper) throws Exception {
    Path path = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("leaf_decay_stopper-common.toml");
    byte[] originalBytes = Files.readAllBytes(path);
    var originalValues = new ArrayList<>(Config.DIMENSIONS_WITHOUT_DECAY.get());
    var level = helper.getLevel();
    var pos = helper.absolutePos(BlockPos.ZERO.above(2));
    var originalBlock = level.getBlockState(pos);
    boolean originalDrops = level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
    level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, level.getServer());
    var clock = new AtomicInteger();
    var stage = new AtomicInteger();
    var expected = List.of("minecraft:the_nether");
    helper.onEachTick(() -> {
      try {
        // GameTestServer ticks can run faster than the real-time file-watcher debounce.
        java.util.concurrent.locks.LockSupport.parkNanos(50_000_000L);
        int tick = clock.incrementAndGet();
        // Restore owned state before the framework's own timeout can end the test.
        helper.assertTrue(tick < 500, "Manual saves must refresh the live config without a restart");
        switch (stage.get()) {
          case 0 -> {
            Files.writeString(path, "dimensionsWithoutDecay = [\"minecraft:the_nether\"]\n");
            stage.set(1);
          }
          case 1 -> {
            if (!Config.DIMENSIONS_WITHOUT_DECAY.get().equals(expected)) return;
            var leaf = Blocks.OAK_LEAVES.defaultBlockState()
                .setValue(LeavesBlock.DISTANCE, 7).setValue(LeavesBlock.PERSISTENT, false);
            level.setBlock(pos, leaf, 3);
            leaf.randomTick(level, pos, RandomSource.create(123));
            helper.assertTrue(level.getBlockState(pos).isAir(), "Ordinary save must enable actual leaf decay");
            Path temporary = path.resolveSibling("leaf-decay-gametest-save.tmp");
            Files.writeString(temporary, "dimensionsWithoutDecay = [\"minecraft:overworld\"]\n");
            Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            stage.set(2);
          }
          case 2 -> {
            if (!Config.DIMENSIONS_WITHOUT_DECAY.get().equals(List.of("minecraft:overworld"))) return;
            var leaf = Blocks.OAK_LEAVES.defaultBlockState()
                .setValue(LeavesBlock.DISTANCE, 7).setValue(LeavesBlock.PERSISTENT, false);
            level.setBlock(pos, leaf, 3);
            leaf.randomTick(level, pos, RandomSource.create(123));
            helper.assertTrue(level.getBlockState(pos).is(Blocks.OAK_LEAVES), "Replacement save must protect actual leaves");
            Files.write(path, originalBytes);
            level.setBlock(pos, originalBlock, 3);
            stage.set(3);
          }
          case 3 -> {
            if (!Config.DIMENSIONS_WITHOUT_DECAY.get().equals(originalValues)) return;
            helper.assertTrue(Arrays.equals(originalBytes, Files.readAllBytes(path)), "Exact original config bytes must be restored");
            var source = level.getServer().createCommandSourceStack().withLevel(level);
            boolean enable = originalValues.contains("minecraft:overworld");
            int result = level.getServer().getCommands().getDispatcher().execute(
                "setleavesdecay minecraft:overworld " + enable, source);
            helper.assertTrue(result > 0, "Commands must still take effect immediately");
            helper.assertTrue(Config.isDecayEnabledForDimension(level) == enable, "Command cache must be current");
            // Re-save the previous file immediately after a command changes the cached value.
            Files.write(path, originalBytes);
            stage.set(4);
          }
          case 4 -> {
            if (!Config.DIMENSIONS_WITHOUT_DECAY.get().equals(originalValues)) return;
            helper.assertTrue(Arrays.equals(originalBytes, Files.readAllBytes(path)), "The saved file must win after a command");
            helper.assertTrue(level.getBlockState(pos).equals(originalBlock), "Original block must be restored");
            level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(originalDrops, level.getServer());
            helper.succeed();
          }
        }
      } catch (Throwable error) {
        try {
          Config.DIMENSIONS_WITHOUT_DECAY.set(originalValues);
          Files.write(path, originalBytes);
          level.setBlock(pos, originalBlock, 3);
          level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(originalDrops, level.getServer());
        } catch (Exception restoreError) {
          error.addSuppressed(restoreError);
        }
        helper.fail(error.toString());
      }
    });
  }
}
