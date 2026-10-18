# Leaf Decay Stopper

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/leafdecaystopper) · [Modrinth](https://modrinth.com/mod/leaf-decay-stopper) · [GitHub releases](https://github.com/CrabMods/leaf_decay_stopper/releases) · [Report a bug](https://github.com/CrabMods/leaf_decay_stopper/issues)

[English](#english) · [中文](#中文)

## English

Keep leaves from decaying in your builds, with separate settings for each dimension. By default, leaves are protected in the Overworld, Nether and End. Crop growth and other blocks' random ticks continue normally.

### Versions and installation

Current release: **1.0.1**.

| Minecraft | Loader |
| --- | --- |
| 1.20.1 | Forge |
| 1.21.1 | NeoForge |

Download the file for your Minecraft version and loader, then put it in the `mods` folder.

- **Multiplayer:** install it on the server. Players do not need to install it on their clients.
- **Singleplayer:** install it in your client instance. Enable cheats to use the command.

Installing the mod only on a client does not change leaf decay on a remote server.

### Commands

Protect leaves in the Overworld:

```text
/setleavesdecay minecraft:overworld false
```

Allow normal leaf decay again:

```text
/setleavesdecay minecraft:overworld true
```

`false` stops decay; `true` allows it. Replace `minecraft:overworld` with another loaded dimension's ID, including a custom dimension. Tab completion lists the available dimensions. Server players need operator permission level 2; the server console can also run the command.

Changes take effect immediately and are saved to the config file. Leaves that have already decayed are not restored.

### Configuration

The mod creates `config/leaf_decay_stopper-common.toml` when it first runs. The default setting is:

```toml
dimensionsWithoutDecay = ["minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"]
```

Leaves are protected in the listed dimensions. Dimensions outside the list use normal decay. To allow decay everywhere, use:

```toml
dimensionsWithoutDecay = []
```

Use full dimension IDs, such as `minecraft:overworld` or `yourmod:your_dimension`. Save the file to apply changes without restarting.

The config belongs to the server or client instance, rather than an individual world. In singleplayer, the same settings apply to every save opened in that instance. Command changes update this shared config too.

### Compatibility and bug reports

Vanilla leaves and representative Biomes O' Plenty leaves have been tested. Mods that replace the normal leaf-decay behavior may need a separate compatibility check.

If something goes wrong, [open an issue](https://github.com/CrabMods/leaf_decay_stopper/issues) with your Minecraft version, loader version, mod version and what happened. For a crash or modpack-specific problem, include `logs/latest.log`, the crash report if there is one, and the modpack name and version.

### Changes in 1.0.1

Fixed dimension validation and config handling, repaired Forge config hot reload, and corrected older Forge startup compatibility and update information. See the [release notes](docs/releases/1.0.1.md) for the full list.

## 中文

让建筑和景观中的树叶保持原样，并按维度决定是否允许自然凋零。默认保护主世界、下界和末地的树叶，不影响作物生长和其他方块的随机刻更新。

### 版本与安装

当前版本：**1.0.1**。Minecraft **1.20.1 使用 Forge**，**1.21.1 使用 NeoForge**。下载对应文件，放入 `mods` 文件夹。

- **多人游戏：**安装在服务端即可，玩家客户端不必安装。
- **单人游戏：**安装在自己的游戏实例中。使用指令需要开启作弊。

只在客户端安装，无法改变远程服务器上的树叶凋零规则。

### 指令

保护主世界的树叶：

```text
/setleavesdecay minecraft:overworld false
```

恢复主世界的正常树叶凋零：

```text
/setleavesdecay minecraft:overworld true
```

`false` 表示停止凋零，`true` 表示允许凋零。将 `minecraft:overworld` 换成其他已加载维度的 ID，就能调整对应维度，也支持自定义维度。输入时可以用 Tab 补全。服务器玩家需要 OP 2 级权限，也可以从服务端控制台执行。

修改立即生效，并自动写入配置文件。已经凋零的树叶不会恢复。

### 配置文件

首次运行后会生成 `config/leaf_decay_stopper-common.toml`，默认内容如下：

```toml
dimensionsWithoutDecay = ["minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"]
```

列表中的维度会受到保护，其他维度保持正常凋零。若希望所有维度都恢复正常凋零，将列表留空：

```toml
dimensionsWithoutDecay = []
```

请使用完整的维度 ID，例如 `minecraft:overworld` 或 `yourmod:your_dimension`。手动修改文件后保存即可生效，无需重启。

配置属于整个服务端或游戏实例，不是某个存档的独立设置。单人游戏中，同一实例下的所有存档共用这份配置；指令也会修改这份配置。

### 兼容性与问题反馈

已测试原版树叶和 Biomes O' Plenty 的部分树叶。若其他模组替换了原本的树叶凋零逻辑，需要单独确认兼容性。

遇到问题时，请在 [Issues](https://github.com/CrabMods/leaf_decay_stopper/issues) 中说明 Minecraft、加载器和本模组的版本，以及具体发生了什么。如果是崩溃或整合包中的问题，请附上 `logs/latest.log`、崩溃报告（如有），以及整合包名称和版本。

### 1.0.1 更新

修复了维度检查和配置处理问题、Forge 配置热重载、旧版 Forge 的启动兼容性，以及更新信息。完整列表见[更新说明](docs/releases/1.0.1.md)。

## License / 许可证

Both versions use [GPL-3.0](LICENSE). Forge 和 NeoForge 版本均采用 GPL-3.0。
