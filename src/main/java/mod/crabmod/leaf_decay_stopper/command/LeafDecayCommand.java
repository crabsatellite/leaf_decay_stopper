package mod.crabmod.leaf_decay_stopper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import mod.crabmod.leaf_decay_stopper.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LeafDecayCommand {

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

    dispatcher.register(
        Commands.literal("setleavesdecay")
            .requires(source -> source.hasPermission(2))
            .then(
                Commands.argument("enabled", BoolArgumentType.bool())
                    .executes(LeafDecayCommand::executeToggleLeafDecay)));
  }

  private static int executeToggleLeafDecay(CommandContext<CommandSourceStack> context) {
    boolean enabled = BoolArgumentType.getBool(context, "enabled");

    Config.DECAY_ENABLED.set(enabled);
    Config.SPEC.save();

    context
        .getSource()
        .sendSuccess(
            () ->
                Component.translatable("command.leaf_decay." + (enabled ? "enabled" : "disabled")),
            true);

    return 1;
  }
}
