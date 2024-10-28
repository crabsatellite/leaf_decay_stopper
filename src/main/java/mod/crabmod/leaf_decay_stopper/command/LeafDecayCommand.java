package mod.crabmod.leaf_decay_stopper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.List;
import mod.crabmod.leaf_decay_stopper.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LeafDecayCommand {

  // Suggestion provider for dimension names
  private static final SuggestionProvider<CommandSourceStack> DIMENSION_SUGGESTION_PROVIDER =
      (context, builder) -> {
        // Provide dimension suggestions only when "dimension" parameter is being input
        for (ResourceKey<Level> dimension : context.getSource().getServer().levelKeys()) {
          builder.suggest(dimension.location().toString());
        }
        return builder.buildFuture();
      };

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

    dispatcher.register(
        Commands.literal("setleavesdecay")
            .requires(source -> source.hasPermission(2))
            .then(
                Commands.argument(
                        "dimension",
                        ResourceLocationArgument
                            .id()) // Parse namespace using ResourceLocationArgument
                    .suggests(DIMENSION_SUGGESTION_PROVIDER) // Add dimension name autocomplete
                    .then(
                        Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(LeafDecayCommand::executeToggleLeafDecay))));
  }

  private static int executeToggleLeafDecay(CommandContext<CommandSourceStack> context) {
    ResourceLocation dimension = ResourceLocationArgument.getId(context, "dimension");
    boolean enabled = BoolArgumentType.getBool(context, "enabled");

    // Create a mutable list to modify dimensions without leaf decay
    List<String> dimensionsWithoutDecay = new ArrayList<>(Config.DIMENSIONS_WITHOUT_DECAY.get());
    boolean currentlyDisabled = dimensionsWithoutDecay.contains(dimension.toString());

    // Check if the dimension is already in the desired state
    if (enabled && !currentlyDisabled) {
      // Leaf decay is already enabled for this dimension
      context
          .getSource()
          .sendSuccess(
              () ->
                  Component.translatable(
                      "command.leaf_decay.already_enabled", dimension.toString()),
              true);
      return 1;
    } else if (!enabled && currentlyDisabled) {
      // Leaf decay is already disabled for this dimension
      context
          .getSource()
          .sendSuccess(
              () ->
                  Component.translatable(
                      "command.leaf_decay.already_disabled", dimension.toString()),
              true);
      return 1;
    }

    // Perform the actual action
    if (enabled) {
      dimensionsWithoutDecay.remove(dimension.toString());
    } else {
      dimensionsWithoutDecay.add(dimension.toString());
    }

    // Update and save the configuration
    Config.DIMENSIONS_WITHOUT_DECAY.set(dimensionsWithoutDecay);
    Config.SPEC.save();

    // Send feedback after updating the state
    context
        .getSource()
        .sendSuccess(
            () ->
                Component.translatable(
                    "command.leaf_decay." + (enabled ? "enabled" : "disabled"),
                    dimension.toString()),
            true);

    return 1;
  }
}
