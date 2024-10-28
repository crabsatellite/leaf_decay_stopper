package mod.crabmod.leaf_decay_stopper.provider;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class DimensionSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
      CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    for (ResourceKey<Level> dimension : context.getSource().getServer().levelKeys()) {
      builder.suggest(dimension.location().toString());
    }
    return builder.buildFuture();
  }
}
