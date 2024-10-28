## Leaf Decay Stopper Mod

### Overview
The *Leaf Decay Stopper* mod allows players to control leaf decay based on dimensions in Minecraft. This mod enables or disables leaf decay within specific dimensions through commands, allowing players to keep leaves intact for landscaping or decorative purposes. By default, leaf decay is disabled in the Overworld, Nether, and End dimensions.

### Features

- **Dimension-Specific Control**: Choose if leaves decay independently in each dimension, including custom dimensions.
- **Command-Based**: Set leaf decay dynamically with simple in-game commands.
- **Instant Feedback**: Immediate feedback lets you know if your desired state has been set.
- **Default Decay Control**: By default, leaf decay is disabled in the Overworld, Nether, and End dimensions.

### Commands

The mod introduces a command, `/setleavesdecay`, to toggle leaf decay status:

```
/setleavesdecay [dimension] [enabled]
```

#### Parameters

- **dimension**: The name of the target dimension (e.g., `minecraft:overworld`), with auto-completion suggestions available.
- **enabled**: Boolean (`true` to enable leaf decay, `false` to disable it).

#### Example Usage

- Enable leaf decay in the Overworld:
  ```
  /setleavesdecay minecraft:overworld true
  ```

- Disable leaf decay in the Overworld:
  ```
  /setleavesdecay minecraft:overworld false
  ```

#### Execution Feedback
After execution, the player receives feedback on whether the leaf decay status has been updated for the target dimension. If the current decay status already matches the setting, the command will notify you.

### Installation & Configuration

1. **Download the Mod**: Ensure the downloaded mod is compatible with your Minecraft and Forge version.
2. **Install Forge**: Run the compatible version of Minecraft Forge.
3. **Place the Mod File**: Place the mod file in the `mods` folder.
4. **Launch Game**: Run Minecraft and enter your world.

### Configuration File

The mod generates a configuration file where you can adjust each dimension’s leaf decay status manually if needed. Alternatively, players can use commands to update the configuration dynamically, and changes will be saved automatically.

### Why This Mod?

Ever built a stunning leaf-decorated structure in a custom dimension only to watch your hard work vanish? Or maybe you brought a massive map from another world and need an easier solution than setting individual block persistence with WorldEdit?

Interestingly, there was no mod for recent versions focused on preserving leaf blocks instead of speeding up their decay. So we spent an afternoon developing *Leaf Decay Stopper* to use on our own server and decided to share it.

If you spot any bugs, please let us know! This mod currently supports only Minecraft version 1.20.1, but if demand grows, we may consider other versions.

---

### For developers and Advanced Users

For developers interested in how this mod achieves its functionality, here is an overview of the implementation:

The mod uses a Mixin on `LeavesBlock` to intercept the `randomTick` method, which controls the leaf decay mechanism. The `randomTick` event is only allowed to proceed if decay is enabled in the specific dimension. Otherwise, the event is canceled, preventing decay. 

#### Potential Limitations

- **Mod Compatibility**: Some mods that modify `LeavesBlock` behavior might conflict with this Mixin approach if they also rely on `randomTick` for leaf manipulation.

This solution is simple, efficient for our purpose, and can be modified to accommodate further changes if needed for different gameplay contexts.

## Leaf Decay Stopper 模组

### 简介
Leaf Decay Stopper 模组让玩家能够在 Minecraft 中按维度控制树叶是否凋零。此模组支持在游戏中使用指令，快速启用或禁用特定维度的树叶凋零效果，使树叶能够保留在理想位置以用于景观或装饰。默认情况下，该模组禁用了主世界（Overworld）、下界（Nether）和末地（End）的树叶凋零。

### 功能

- **维度级别的凋零控制**：可以独立控制每个维度的树叶是否凋零，包括自定义维度。
- **基于指令的操作**：使用简便的指令即可动态设置树叶的凋零状态。
- **即时反馈**：指令执行后立即获得反馈，显示当前状态是否已更改。
- **默认状态**：主世界、下界和末地维度中的树叶凋零默认禁用。

### 指令说明

模组新增了 `/setleavesdecay` 指令，用于切换树叶的凋零状态：

```
/setleavesdecay [dimension] [enabled]
```

#### 参数说明

- **dimension**：目标维度的名称（例如 `minecraft:overworld`），支持自动补全建议。
- **enabled**：布尔值，`true` 表示启用树叶凋零，`false` 表示禁用。

#### 示例用法

- 启用主世界的树叶凋零：
  ```
  /setleavesdecay minecraft:overworld true
  ```

- 禁用主世界的树叶凋零：
  ```
  /setleavesdecay minecraft:overworld false
  ```

#### 执行反馈
指令执行后，玩家会收到反馈提示，显示目标维度的树叶凋零状态是否已更新。如当前凋零状态已与设置一致，指令会提示已处于该状态。

### 安装与配置

1. **下载模组**：确保下载的模组文件与当前的 Minecraft 和 Forge 版本兼容。
2. **安装 Forge**：运行相应版本的 Minecraft Forge。
3. **放置模组文件**：将模组文件放入 `mods` 文件夹。
4. **启动游戏**：运行 Minecraft 并进入世界。

### 配置文件

模组会自动生成配置文件，允许手动调整每个维度的树叶凋零状态。玩家也可以使用指令动态更新配置，更新的状态将自动保存。

### 为什么需要这个模组？

你是否曾在自定义维度中精心打造了一个装饰性叶子结构，却眼睁睁看着树叶逐渐凋零消失？或者将一大片地图导入后，发现要用 WorldEdit 为每片叶子设置持久状态十分繁琐？在 WorldEdit 中使叶子持久化，需要手动修改每个叶块的元数据，这对大规模建筑来说不仅麻烦，还可能导致性能问题。

调整 randomTickSpeed 为 0 确实可以阻止叶子凋零，但这会影响作物生长和其他需要随机刻更新的元素，效果欠佳。此外，近期也没有直接关注叶子保留的模组，多数模组更偏向加速凋零速度，因此可用的选项十分有限。所以，我们开发了 Leaf Decay Stopper 来解决这些问题。这个模组无需逐一更改方块或完全禁用随机刻更新，可以有效保留树叶，非常适合需要保持植被美观的建筑师和景观设计者。

目前该模组仅支持 Minecraft 1.20.1。如果发现任何 bug，欢迎反馈。如有更多版本需求，我们会考虑支持更多版本。

---

### 实现方法和可能的限制

对于有兴趣了解实现方式的开发者，这里概述了该模组的主要实现思路：

模组使用了 `LeavesBlock` 的 Mixin，以拦截控制树叶凋零的 `randomTick` 方法。只有当目标维度的配置允许凋零时，`randomTick` 事件才会继续执行。否则，事件将被取消，从而阻止树叶凋零

#### 可能的限制

- **模组兼容性**：一些修改 `LeavesBlock` 行为的模组可能会与此 Mixin 实现存在冲突，特别是在依赖 `randomTick` 进行叶子操作时。

此解决方案简单高效，适合我们当前的需求，并可在不同场景下灵活调整。
