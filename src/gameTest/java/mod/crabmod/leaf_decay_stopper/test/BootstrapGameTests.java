package mod.crabmod.leaf_decay_stopper.test;

import java.nio.file.Files;
import java.util.List;
import mod.crabmod.leaf_decay_stopper.Config;
import mod.crabmod.leaf_decay_stopper.LeafDecayStopper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("leaf_decay_stopper")
@PrefixGameTestTemplate(false)
public final class BootstrapGameTests {
  @GameTest(templateNamespace = "leaf_decay_stopper", template = "empty", batch = "bootstrap_registration", timeoutTicks = 20)
  public static void loader_registered_the_common_configuration(GameTestHelper helper) throws Exception {
    helper.assertTrue(net.minecraftforge.fml.ModList.get().isLoaded(LeafDecayStopper.MODID), "The loader must load the mod");
    helper.assertTrue(Config.SPEC.isLoaded(), "The loader must attach the common config specification");
    var path = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve(LeafDecayStopper.MODID + "-common.toml");
    helper.assertTrue(Files.isRegularFile(path), "The registered common config file must exist");
    var document = new com.electronwill.nightconfig.toml.TomlParser().parse(Files.readString(path));
    helper.assertTrue(document.get("dimensionsWithoutDecay") instanceof List<?>, "The loader must initialize the dimension list");
    helper.succeed();
  }
}
