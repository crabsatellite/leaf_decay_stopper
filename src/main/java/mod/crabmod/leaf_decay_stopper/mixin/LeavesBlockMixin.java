package mod.crabmod.leaf_decay_stopper.mixin;

import mod.crabmod.leaf_decay_stopper.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

  @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
  public void onRandomTick(
      BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
    if (!Config.isDecayEnabledForDimension(world)) {
      ci.cancel();
    }
  }
}
