# REMOVED-FEATURES — Leaves 26.1.2 库特性债

26.1.2 采用 Leaves-26.2 的特性集（参照 `docs/deferred-26.2/REMOVED-FEATURES.md`）：上游 26.x 已移除对应支撑 API，这些库包在本版本被删除而不是被硬塞回，补丁本体留在 `docs/deferred-26.1.2/`。

| 删除的库路径 | 原因 |
| --- | --- |
| `org/leavesmc/leaves/command/arguments/BotArgumentType.java`、`command/bot/**` | CraftBot/Aikar timings 与 bot API 在 26.x 无支撑（deferred ps 0013） |
| `org/leavesmc/leaves/command/leaves/subcommands/CounterCommand.java` | HopperCounter 库被删 |
| `org/leavesmc/leaves/entity/photographer/**` | photographer 依赖已删的 fakeplayer/协议层支撑 |
| `org/leavesmc/leaves/lithium/api/inventory/*`、`lithium/common/**` | Lithium 补丁层整体 deferred（0131/0133 等） |
| `org/leavesmc/leaves/network/AsyncKeepaliveManager.java` | Paper 26.x 自带 keepalive 路径，deferred 0140 |
| `org/leavesmc/leaves/replay/**` | Replay Mod API deferred（api 侧接口保留） |
| `org/leavesmc/leaves/util/HopperCounter.java`、`util/WoolUtils.java`、`util/ReturnPortalManager.java` | 依赖 1.21.8-only 的 wool/portal 补丁语义 |

本版本另外延期的语义（不是删库，而是改写不成）：

- `vanillaHopper`（`HopperBlockEntity.hopperPull` 语义）—— 26.1.2 的 `callPushMoveEvent` 结构不同，见 `0025` misanchor 补丁裁决。
- CraftPlayer `unsetFixedPose` / Pose API —— 26.1.2 `Player.java` 无 `fixedPose` 字段且 `updatePlayerPose()` 为 protected。
- moonrise region IO 委托（`moonrise$getRecalculateCount` / `moonrise$recalculateHeader`）—— 26.1.2 无该 accessor，本版本保留 plain 实现，见 `docs/deferred-26.1.2/minecraft-features` 里的 0023 compat 补丁。
- REI `MapCloningRecipe` / `TippedArrowRecipe`、`LinearRegionFile` stub、Technical Survival Mode authlib、`FertilizableCoral` —— 随 mc 层 deferred。
