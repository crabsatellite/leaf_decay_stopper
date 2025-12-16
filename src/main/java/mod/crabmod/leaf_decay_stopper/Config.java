package mod.crabmod.leaf_decay_stopper;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
  public static final ModConfigSpec SPEC;

  public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS_WITHOUT_DECAY;

  static {
    DIMENSIONS_WITHOUT_DECAY =
        BUILDER
            .comment("Specify dimensions where leaf decay should be disabled.")
            .defineList(
                "dimensionsWithoutDecay",
                List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"),
                obj -> obj instanceof String);

    SPEC = BUILDER.build();
  }

  public static void onLoad(final ModConfigEvent event) {
    // Configuration loaded event, no additional setup required here
  }

  /**
   * Check if leaf decay is enabled in the given dimension.
   *
   * @param level The level or dimension to check.
   * @return true if leaf decay is enabled in this dimension, false otherwise.
   */
  public static boolean isDecayEnabledForDimension(Level level) {
    ResourceLocation dimensionKey = level.dimension().location();
    List<? extends String> dimensionsWithoutDecay = DIMENSIONS_WITHOUT_DECAY.get();
    return !dimensionsWithoutDecay.contains(dimensionKey.toString());
  }
}
