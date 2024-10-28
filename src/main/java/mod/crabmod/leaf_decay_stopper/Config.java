package mod.crabmod.leaf_decay_stopper;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LeafDecayStopper.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.BooleanValue DECAY_ENABLED;

  static {
    DECAY_ENABLED = BUILDER
            .comment("Enable or disable leaf decay. true: leaves will decay. false: leaves will not decay.")
            .define("decayEnabled", false);

    SPEC = BUILDER.build();
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    // No need to update any local variable, as we can directly use DECAY_ENABLED.get() when needed
  }
}
