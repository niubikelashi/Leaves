# 26.2 本轮整特性撤回清单（round46）

裁决依据：bot（fakeplayer）的 mc/paper 挂钩在版本适配阶段已整体延期到 `docs/deferred-26.2/`，
落地源码中没有任何 mc 侧 bot 代码，因此这些依赖 bot 的自有源码在 26.2 上无法编译。
先跑通版本链，之后按原始补丁回填语义。

| 特性 | 撤回内容 | 原始补丁（可 `git show ffb9cc5:<path>` 取回） |
| --- | --- | --- |
| Bot / Fakeplayer | `leaves/bot/**`、`entity/bot/**`、`command/bot/**`、`command/arguments/BotArgumentType.java`；`LeavesConfig` fakeplayer 校验器与 tick-type/simulation-distance；`AsyncKeepaliveManager`、`LeavesMinecraftSessionService`、`PcaSyncProtocol`、`UpdateSuppressionException` 的 bot 分支 | `minecraft-patches/features/0007-Leaves-Fakeplayer.patch`（已延期） |
| Photographer / Replay | `replay/**`（Recorder/ServerPhotographer/ReplayFile 等）、`entity/photographer/**`；`CraftServer.getPhotographerManager()` 暂返回 null | `paper-patches/features/0010-Replay-Mod-API.patch`（已延期） |
| Wool Hopper Counter | `util/HopperCounter.java`、`command/leaves/subcommands/CounterCommand.java`、`LeavesCommand` 注册；26.2 的 `Blocks` 已无 `*_WOOL`/`*_*_TERRACOTTA` 常量且 `BlockBehaviour` 无 `getMapColor()` | `minecraft-patches/features/0075-Wool-Hopper-Counter.patch`（已延期） |
| Delete Timings（server 层） | `PaperEventManager` 的 `co.aikar.timings.TimedEventExecutor` 引用、`PaperPluginManagerImpl.useTimings()` 改为固定 false | `paper-patches/features/0002-Delete-Timings.patch` |

语义等价性说明：
- `ServerLevel.realPlayers()` 在没有 bot 的前提下等价于 `players()`（本轮在 mc 层新增该方法以补 Paper 侧调用点）。
- `CraftServer.getBotManager()` / `getPhotographerManager()` 保留 API 声明但返回 null；api 模块中的
  `BotManager` / `PhotographerManager` 接口仍存在，插件读取时会得到 null，属已知债务。
- `CraftPlayer.getChannel()` 返回 `getHandle().connection.connection.channel`（netty Channel），
  与 `InternalBytebufHandler` 的 `(Channel) audience.getChannel()` 用法一致。
