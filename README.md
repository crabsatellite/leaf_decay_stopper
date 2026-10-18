# Leaf Decay Stopper

<img src="https://raw.githubusercontent.com/CrabMods/leaf_decay_stopper/forge-1.20.1-47.3.11/images/logo_512.png" width="128" alt="Leaf Decay Stopper icon">

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/leafdecaystopper) · [Modrinth](https://modrinth.com/mod/leaf-decay-stopper) · [GitHub releases](https://github.com/CrabMods/leaf_decay_stopper/releases) · [Report a bug](https://github.com/CrabMods/leaf_decay_stopper/issues)

Keep leaves from decaying in your builds, with separate settings for each dimension. By default, leaves are protected in the Overworld, Nether and End. Crop growth and other blocks' random ticks continue normally.

> **Full player guide:** For advanced features, complete recipes, detailed effects, configuration and compatibility, visit the [official Leaf Decay Stopper guide](https://crabmods.com/mods/leafdecay).

## Versions and installation

| Minecraft | Loader |
| --- | --- |
| 1.20.1 | Forge |
| 1.21.1 | NeoForge |

Download the file for your Minecraft version and loader, then put it in the `mods` folder.

- **Multiplayer:** install it on the server. Players do not need to install it on their clients.
- **Singleplayer:** install it in your client instance. Enable cheats to use the command.

Installing the mod only on a client does not change leaf decay on a remote server.

## Commands

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

## Compatibility and Bug Reports

Protection applies to leaves using vanilla's normal leaf random-tick path. Other blocks keep their usual random ticks; mods with a separate decay implementation need a compatibility check.

The settings belong to the server or client instance, rather than an individual world. In singleplayer, the same settings apply to every save opened in that instance.

## More details

For more advanced features, full recipes, effect values, configuration and compatibility help, see the **[official player guide](https://crabmods.com/mods/leafdecay)**.

[Downloads and release notes](https://github.com/CrabMods/leaf_decay_stopper/releases) · [Report a bug](https://github.com/CrabMods/leaf_decay_stopper/issues)

License: [GPL-3.0](LICENSE).
