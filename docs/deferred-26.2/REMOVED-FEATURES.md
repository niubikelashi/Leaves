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


## Lithium / Moonrise 优化子系统（26.2 整特性延期）
- 外层 `leaves-server/src/main/java/org/leavesmc/leaves/lithium/**` 已删除并记债。
- 理由：Lithium 依赖 mc 层注入的 `lithium$…`/`moonrise$getRawPalette` 等访问器，来自本轮延期的 mc 补丁（`00xx Lithium-*`、`PalettedContainer`）；
  仓库内没有 mixin 资源（`mixins.json` 命中 0），且已应用补丁层对 lithium/moonrise 的引用为 0，因此无法在 26.2 上机械重建。
- 连带删除的错误文件：`common/hopper/{HopperHelper,LithiumStackList}.java`、`common/block/entity/{SleepingBlockEntity,SleepUntilTimeBlockEntityTickInvoker}.java`、
  `common/block/tracking/ComparatorTracking.java`、`common/world/chunk/{LithiumHashPalette,LithiumStackList 等}.java`。
- Moonrise 本体仍在 Paper 26.2 中（`ca.spottedleaf.moonrise.*`），外层 `util/ElytraAeronauticsHelper.java`、`region/*` 的 moonrise 调用编译通过，未删除。
- 回填计划：按 `docs/deferred-26.2/minecraft-features/` 内的 Lithium 补丁逐特性重建注入方法（或改为 mixin 框架）后再恢复本目录。

## Async Keepalive（26.2 整特性延期）
- 外层 `org/leavesmc/leaves/network/AsyncKeepaliveManager.java` 已删除并记债。
- 理由：该类调用 `ServerCommonPacketListenerImpl.keepConnectionAliveAsync(long, long)` 与 `processedDisconnect`，
  这两个成员在 26.2 全仓命中 0（只有 `protected void keepConnectionAlive()`），依赖延期的 mc 补丁。
- paper-server 层没有任何对 `AsyncKeepaliveManager` 的引用，删除不破坏补丁层。
- 回填计划：实现 mc 层异步 keepalive 方法（含 `Connection` 锁语义）后恢复。


## 26.2 round50：可见性放宽与字段重注入（compat commit，待并入原 feature 补丁）
- `Entity.bukkitEntity` private -> public：paper-server 的 `CraftComplexPart.getParent()` 跨包写入（对应 deferred `Old-ender-dragon-part-can-use-end-portal` 语义）。
- `FireworkRocketEntity.getExplosions()`、`Creeper.droppedSkulls`、`Language.parseTranslations`、
  `CoralFeature/CoralClaw/CoralTree/CoralMushroom.placeFeature`、`BlockPattern.matches(BlockPos,Direction,Direction,LoadingCache)`：放宽为 public，
  与 1.21.8 基线的 `// Leaves - private -> public` 一致；对应补丁仍在 `docs/deferred-26.2/minecraft-features/`。
- `RegionFile` 改为 `implements org.leavesmc.leaves.region.IRegionFile`（并放宽 `recalculateHeader/write/isOversized/setOversized/getOversizedData`）：
  26.2 的 vanilla `RegionFile` 已具备全部方法，只是可见性不足；region 的其余补丁（RegionFileStorage/格式切换）仍 deferred，
  `IRegionFileFactory` 里 `REGION_SHIFT` 内联为常量 5（26.2 不再暴露 `RegionFileStorage.REGION_SHIFT`）。
- `Player.elytraAeronauticsNoChunk` 字段重注入：Elytra Aeronautics 的行为钩子（`ChunkMap` 距离地图、`Entity.tickRiding`、
  `FireworkRocketItem`、`ServerGamePacketListenerImpl` 移动校验）对应的 mc 补丁仍在 deferred，本轮只恢复数据槽位；
  `ElytraAeronauticsHelper` 改用 `level().getChunkSource().chunkMap.getDistanceManager()`（26.2 `ServerLevel.chunkSource` 已 private）。
- `ServerGamePacketListenerImpl.exchangeTarget` 重注入：26.2 已彻底删除 vanilla `ExchangeTarget`（全仓命中 0），
  syncmatica 继续使用 Leaves 自有的 `protocol/syncmatica/exchange/ExchangeTarget`；其余 syncmatica mc/server 补丁仍 deferred。

## 26.2 round50：整特性延期（删除外层文件）
- `org/leavesmc/leaves/util/WoolUtils.java` 删除：26.2 `Blocks` 已不再有 `WHITE_WOOL` 等羊毛常量（羊毛改为 `Blocks.WOOL` + `ItemStack` 组件路线，
  `*_WOOL` 常量全仓命中 0），羊毛漏斗计数没有数据源；依赖它的 `common/hopper` 计数补丁本身也在 deferred。
- `org/leavesmc/leaves/util/ReturnPortalManager.java` 删除：它调用 `Player.getLeavesData()`（玩家持久数据注入），该 mc 补丁仍在 deferred；
  全仓（mc fork / paper-server fork）对该类的引用为 0，删除不破坏补丁层。
- 回填计划：两条都在 `docs/deferred-26.2/minecraft-features/` 的对应补丁落地后恢复。
