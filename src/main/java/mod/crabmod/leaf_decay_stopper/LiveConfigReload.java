package mod.crabmod.leaf_decay_stopper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.toml.TomlParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

/** Repairs old NightConfig's dropped/coalesced and replacement-save events for our file only. */
final class LiveConfigReload implements AutoCloseable {
  private static final Logger LOGGER = LogUtils.getLogger();
  private final ClassLoader contextClassLoader;
  private final ModConfig config;
  private final CommentedFileConfig data;
  private final Path path;
  private final WatchService events;
  private final ScheduledThreadPoolExecutor reloads;
  private ScheduledFuture<?> pending;
  private boolean closed;

  LiveConfigReload(ModConfig config) throws IOException {
    contextClassLoader = Thread.currentThread().getContextClassLoader();
    this.config = config;
    data = (CommentedFileConfig) config.getConfigData();
    path = data.getNioPath().toAbsolutePath();
    events = path.getFileSystem().newWatchService();
    reloads = new ScheduledThreadPoolExecutor(1, task -> daemon(task, "leaf-decay-config-reload"));
    reloads.setRemoveOnCancelPolicy(true);
    try {
      path.getParent().register(events, StandardWatchEventKinds.ENTRY_MODIFY,
          StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
      // Keep the shared directory registration intact. Both notification sources now use
      // the same serialized reload; removing a native watch also cancels other files' key.
      FileWatcher.defaultInstance().setWatch(path, this::changed);
      daemon(this::watch, "leaf-decay-config-watch").start();
    } catch (IOException | RuntimeException error) {
      close();
      throw error;
    }
  }

  private Thread daemon(Runnable task, String name) {
    Thread thread = new Thread(task, name);
    thread.setDaemon(true);
    thread.setContextClassLoader(contextClassLoader);
    return thread;
  }

  private void watch() {
    try {
      WatchKey key;
      while ((key = events.take()) != null) {
        for (var event : key.pollEvents()) {
          // count() is multiplicity, not a reason to discard a real change.
          if (event.kind() == StandardWatchEventKinds.OVERFLOW
              || path.getFileName().equals(event.context())) changed();
        }
        if (!key.reset()) {
          LOGGER.error("Leaf Decay Stopper configuration directory is no longer available");
          return;
        }
      }
    } catch (ClosedWatchServiceException expectedOnUnload) {
      // Closing the config releases the blocking watch.
    } catch (InterruptedException interrupted) {
      Thread.currentThread().interrupt();
    }
  }

  private synchronized void changed() {
    if (closed) return;
    if (pending != null) pending.cancel(false);
    pending = reloads.schedule(this::reload, 200, TimeUnit.MILLISECONDS);
  }

  private synchronized void reload() {
    if (closed || !Files.isRegularFile(path)) return;
    if (config.getSpec().isCorrecting()) {
      changed();
      return;
    }
    try {
      byte[] bytes = Files.readAllBytes(path);
      // Reject incomplete TOML before touching the live file-backed config or its cache.
      new TomlParser().parse(new String(bytes, StandardCharsets.UTF_8));
      data.load();
      if (!Arrays.equals(bytes, Files.readAllBytes(path))) {
        changed();
        return;
      }
      if (!config.getSpec().isCorrect(data)) {
        ConfigFileTypeHandler.backUpConfig(data);
        config.getSpec().correct(data);
        data.save();
      }
      config.getSpec().afterReload();
      ModList.get().getModContainerById(config.getModId()).orElseThrow()
          .dispatchConfigEvent(IConfigEvent.reloading(config));
    } catch (IOException | ParsingException error) {
      LOGGER.warn("Could not reload Leaf Decay Stopper configuration; correct the file and save again", error);
    }
  }

  @Override
  public synchronized void close() throws IOException {
    closed = true;
    reloads.shutdownNow();
    events.close();
  }
}
