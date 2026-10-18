package mod.crabmod.leaf_decay_stopper.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import mod.crabmod.leaf_decay_stopper.LeafDecayStopper;
import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("leaf_decay_stopper")
@PrefixGameTestTemplate(false)
public final class UpdateMetadataGameTests {
  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", timeoutTicks = 20)
  public static void update_document_matches_loader_schema(GameTestHelper helper) throws Exception {
    try (var input = UpdateMetadataGameTests.class.getResourceAsStream("/validation/updates.json")) {
      helper.assertTrue(input != null, "The update document must be included in the test resources");
      var root = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
      String game = SharedConstants.getCurrentVersion().getName();
      String version = net.minecraftforge.fml.ModList.get().getModContainerById(LeafDecayStopper.MODID).orElseThrow().getModInfo().getVersion().toString();
      helper.assertTrue("https://www.curseforge.com/minecraft/mc-mods/leafdecaystopper".equals(string(root, "homepage")),
          "The update checker must link to this mod");
      var promotions = root.getAsJsonObject("promos");
      helper.assertTrue(version.equals(string(promotions, game + "-latest"))
          && version.equals(string(promotions, game + "-recommended")), "Both promotions must match this game and mod version");
      helper.assertTrue(root.has(game) && root.get(game).isJsonObject(), "Release notes must use Minecraft-version keys");
      helper.assertTrue(!string(root.getAsJsonObject(game), version).isBlank(), "A release note must be a nonempty string");
    }
    helper.succeed();
  }

  private static String string(JsonObject object, String key) {
    if (object == null || !object.has(key) || !object.get(key).isJsonPrimitive()
        || !object.getAsJsonPrimitive(key).isString()) {
      throw new IllegalArgumentException("Missing string in update document: " + key);
    }
    return object.get(key).getAsString();
  }
}
