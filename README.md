# Leaf Decay Stopper

Stop natural leaf decay per dimension while other blocks keep their normal random ticks. The Overworld, Nether and End are protected by default.

[Download: CurseForge](https://www.curseforge.com/minecraft/mc-mods/leafdecaystopper) · [Modrinth](https://modrinth.com/mod/leaf-decay-stopper) · [Full guide](https://crabmods.com/mods/leafdecay) · [Releases](https://github.com/CrabMods/leaf_decay_stopper/releases)

## Install and use

- Forge 1.20.1 / Java 17 or NeoForge 1.21.1 / Java 21; download the matching build.
- Multiplayer: install on the server; clients do not need it.
- Singleplayer: install in the client instance.

Protect Overworld leaves:

```text
/setleavesdecay minecraft:overworld false
```

Use `true` to allow decay. Commands require operator level 2 or singleplayer cheats. Changes save to the instance-wide config; already-decayed leaves are not restored.

[Configuration, custom dimensions and compatibility](https://crabmods.com/mods/leafdecay) · [Report an issue](https://github.com/CrabMods/leaf_decay_stopper/issues)

## 中文

按维度关闭树叶自然凋零，不影响其他方块的随机刻。默认保护主世界、下界和末地。

多人游戏安装在服务器即可，单人游戏安装在客户端实例中。选择匹配的 Forge 1.20.1 / Java 17 或 NeoForge 1.21.1 / Java 21 文件。

`/setleavesdecay minecraft:overworld false` 保护主世界树叶，改成 `true` 恢复自然凋零。需要权限等级 2，单人需开启作弊。设置保存在实例共用配置中，不会恢复已凋零的树叶。

[完整指南：配置、自定义维度与兼容](https://crabmods.com/mods/leafdecay)。
