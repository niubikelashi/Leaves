# 26.3 语义改写清单（Compat 期间的手动改写与残留债务）

说明：以下改动分两类。
- **嵌套层改写**：落在 `leaves-server/src/minecraft/java`（mc fork）或 `paper-server` 的 commit，会由 `chain_gen.py` 生成为补丁文件，属于补丁层。
- **外层改写**：落在 `leaves-server/src/main/java/org/leavesmc/leaves/**`（Leaves 自有源码），不产生补丁文件，属于外层 commit。

判定标准：gradle `apply*Patches` 后嵌套 repo 的 `HEAD^{tree}` 必须与手工重放 chain 得到的 tree 完全一致且 `dirty=0`。

## 一、补丁层（嵌套 commit → 补丁文件）

### mc fork（base `8dc1e41`，层 `E:\mc\leaves-work\mc-layer-263e`，43 patches / deferred 109）
证据日志：`26.3-chain-mc6.txt`、`26.3-mc-replay-c.txt`、`26.3-r52-region.txt`、`26.3-r53-syncmatica.txt`。

| 补丁 | 内容 | 语义说明 / 债务 |
| --- | --- | --- |
| `0037-Fix-26-2-mis-anchored-Leaves-lines-and-defer-vanilla-hopper` | 把 26.2 期间错位的 Leaves 行重新锚定（bow ticker、gossip、`ServerLevel.realPlayers` 等），并把 vanilla hopper 的 `HopperBlockEntity` 改写整段延期 | **债务**：`HopperBlockEntity` 在 26.3 被重写，原 hunk 无法机械锚定；`0022-Vanilla-hopper.patch` 仅保留非 `HopperBlockEntity` 部分 |
| `0038-fix-compat-26-2-bow-ticker-gossip-ServerLevel-realPlayers` | 同上，兼容重锚定 | 无 |
| `0040-round50-visibility-relax-elytra-syncmatica-field-reinjection-EntityTypes-and-NeutralMob-anchors` | 可见性放宽（private→public）+ 被上游删除字段的重新注入（`exchangeTarget` 字段声明、elytra/syncmatica 相关）+ `EntityTypes`/`NeutralMob` 锚点修正 | **债务**：字段声明重注入时构造函数赋值 hunk 锚定失败，由 round53 补上 |
| `0041-round50b-BlockPattern-cache-overload-private-public` | `BlockPattern` 缓存重载可见性放宽 | 无 |
| `0042-round51-loot-visibility-relax-for-Jade-collector-26-3-loot-refactor` | 26.3 loot 重构后的可见性放宽：`LootPool.entries`、`LootTable.pools`、`CompositeEntryBase.children`、`NestedLootTable.value`、`CompositeLootItemCondition.terms`、`LootPoolEntryContainer.condition` → `public`（注释 `// Leaves - Jade loot traversal`） | **债务**：Jade collector 只处理 `AnyOfCondition`，`AllOf` 多条件未复刻 |
| `0043-round52-region-compat-plain-IRegionFile-accessors-delegate-to-moonrise-API-26-3` | 26.3 `RegionFile` 被 Moonrise 重写，`IRegionFile` 侧的 plain 方法缺失 → 在 mc fork `RegionFile.java` 增加 compat 包装：`getRecalculateCount()` → `moonrise$getRecalculateCount()`；`recalculateHeader()` → `moonrise$recalculateHeader()`；`isOversized(x,z)` 用 `getOffset/getSectorNumber/isExternalStreamChunk` 读扇区头；`getOversizedData` 抛 IOException（Moonrise 无 oversized 概念）；`setOversized` no-op | **债务**：`getOversizedData`/`setOversized` 是 stub，依赖它们的旧行为（oversized 数据读写）在 26.3 不可恢复；`isOversized` 语义为「外部流块 = oversized」的近似 |
| `0044-round53-syncmatica-exchangeTarget-ctor-assignment-26-3-re-anchor` | `ServerGamePacketListenerImpl` 构造函数补 `this.exchangeTarget = new ExchangeTarget(this);`（与 baseline `0044-Syncmatica-Protocol.patch` 一致） | 修复 round50 只重注入字段声明导致的「可能尚未初始化变量 exchangeTarget」 |

### paper-server（base `7d6d0fbf4`，层 `pp263-ps-layer`，15 patches / deferred 5）
证据日志：`26.3-chain-ps.txt`、`26.3-ps-replay.txt`、`26.3-ps-fix.txt`、reject 目录 `E:\mc\leaves-work\26.3-rej-ps`。

| 项 | 内容 | 债务 |
| --- | --- | --- |
| `CraftWorld.java` receivers hunk | `Fakeplayer` 补丁的 hunk#5 上下文已变 → 手工重锚定为 `this.getHandle().realPlayers()` | 无 |
| `CraftPlayer.java` 类尾 Bytebuf API 块 | `fix-compat` 补丁尾部（`send(PacketType, Bytebuf)` → `InternalBytebufHandler.sendPacket`；`getChannel()` → `getHandle().connection.connection.channel`）在 26.3 上锚定失败 → 手工追加 | 依赖 `Connection.channel` / `ServerCommonPacketListenerImpl.connection` 均为 public（已核实存在） |
| `PaperEventManager.java` hunk#1 | 只删除 `import co.aikar.timings.TimedEventExecutor;`；Timings 补丁整体已延期（`0002-Delete-Timings`）→ 该 hunk 是 no-op，未落地 | 记录为无影响 |

## 二、外层源码改写（不产生补丁文件）

证据日志：`26.3-r52b-outer.txt`、`26.3-r52d-fix.txt`、`26.3-loot-relax.txt`、`26.3-ps-fix.txt`。

| 文件 | 改写 | 原因 / 债务 |
| --- | --- | --- |
| `bytebuf/WrappedBytebuf.java` | `ItemStack.asBukkitMirror()` 实例方法 → 静态 `CraftItemStack.asBukkitMirror(nms)`；`map(...::asBukkitMirror)` | 26.3 删除实例转换方法 |
| `protocol/jade/accessor/BlockAccessor.java` | `StreamCodec.of(FriendlyByteBuf::writeBlockHitResult, ...readBlockHitResult)` → `net.minecraft.world.phys.BlockHitResult.STREAM_CODEC` | 26.3 `FriendlyByteBuf` 不再有这两个方法 |
| `protocol/rei/display/Display.java` | `TagSlotDisplay s -> ofItemTagHolders(s.tag())`（新增按 `HolderSet<Item>` 展开的方法）；`ContextMap` 迁到 `net.minecraft.util.context.ContextMap` 并用 `builder().set(SlotDisplayContext.REGISTRIES, access).buildAndValidate(SlotDisplayContext.CONTEXT)` | `SlotDisplay.TagSlotDisplay` 现在携带 `HolderSet<Item>`；`ContextMap.Builder` 的公开构造/`withParameter` 已不存在 |
| `protocol/rei/transfer/NewInputSlotCrafter.java` | 取/放物品改用静态 `CraftItemStack.asBukkitMirror` / `asNMSCopy` | 同 WrappedBytebuf |
| `protocol/servux/litematics/utils/SchematicPlacingUtils.java` | `PositionUtils.getRelativeEndPositionFromAreaSize(regionSize)` 已返回 `BlockPos`，去掉多余的 `new BlockPos(...)` 包裹 | 签名漂移 |
| `protocol/servux/ServuxHudDataProtocol.java` | `Recipe.CODEC.encodeStart(NbtOps.INSTANCE, Holder.direct(recipeEntry.value()))` | `Recipe.CODEC` 变为 `Codec<Holder<Recipe<?>>>`，`RecipeHolder<?>` 只给出 `Holder<capture-of-?>`；用 `Holder.direct` 保持同一 NBT 输出 |
| `util/ElytraAeronauticsHelper.java` | 删除 `firework.attachedToEntity.hurtMarked = true;` | `Entity.hurtMarked` 已删除；26.3 vanilla 用 `player.connection.send(new ClientboundSetEntityMotionPacket(player))` + `ServerEntity` motion 同步（该文件第 45 行已经对 `ServerPlayer` 发送 motion 包，行为等价） |
| `util/FertilizableCoral.java` | `BonemealableBlock` 三个方法新增第 5 参 `BonemealSource`；珊瑚生成改为直接构造 `SimpleBlockFeature(Holder.direct(BlockStateProvider.of(properBlock)))` + `PlacedFeature(...)` 并用 `coral.place(worldIn, worldIn.getChunkSource().getGenerator(), random, pos)` | **债务**：vanilla 的 `OverlayFeature` / `coral/block_decoration` 装饰与 `coralAllowed` `BlockPredicateFilter` 未复刻（26.3 registry 里只有 `coral_claw`/`coral_tree`） |
| `protocol/jade/util/LootTableMineableCollector.java` | `isCorrectConditions(Optional<Holder<LootItemCondition>>, ItemStack)`、`MatchTool`（record）测试、遍历 `AnyOfCondition.terms`、`AlternativesEntry.children`、`NestedLootTable.value` | **债务**：`AllOf` 多条件不处理 |
| `region/linear/LinearRegionFile.java` | 新增 moonrise compat：`moonrise$getRecalculateCount` / `moonrise$recalculateHeader` / `moonrise$tryRecalcHeader(int)`（返回 `Boolean.valueOf(this.recalculateHeader())`）。`moonrise$startWrite` 保留文件第 637 行的既有实现（26.2 compat），不重复定义 | 无 |
| `profile/LeavesAuthenticationService.java` | **全量重写**：`com.destroystokyo.paper.profile.PaperAuthenticationService` 在 authlib 10 中已删除 → 改 `extends PaperServicesDiscoveryService`，构造函数 `(Proxy, boolean, Supplier<DiscoveryResponse>)`，static `create(...)` 不再调用父类 private static（`createDiscoverySupplier` / `determineEnvironment`），由调用方传 supplier；`createMinecraftSessionService()` 返回 `LeavesMinecraftSessionService` | **债务**：Extra Yggdrasil 的 mc 补丁已延期，本类当前无引用者（仅 `LeavesConfig.java:1102` 调 `LeavesMinecraftSessionService.initExtraYggdrasilList`） |
| `profile/LeavesMinecraftSessionService.java` | **全量重写**：`extends PaperMinecraftSessionService`；`hasJoinedServer(String, String, InetAddress)` 先走 super，再在 `LeavesConfig.mics.yggdrasil.enable` 且结果为 null 时逐个请求 extra yggdrasil URL；用 `HttpDiscoveryService.constantURL/buildQuery/concatenateURL`（authlib 10 已删 `HttpAuthenticationService`）、`MinecraftClient.get(url, HasJoinedMinecraftServerResponse.class)`、`NameAndId`（record）+ `MinecraftServer.services().nameToIdCache()`（loginProtect）、`MutablePropertyMap`、`Set<ProfileActionType>` → `new ProfileResult(profile, actions)`；`MinecraftClientException.toAuthenticationException()` 仅在 `AuthenticationUnavailableException` 时抛出 | **债务**：baseline 的 yggdrasil 属性校验/签名校验细节（原 mc 补丁）未复刻 |

## 三、被丢弃的失败 hunk（`--reject` 重放中被静默丢弃的部分）

| 补丁 | hunk | 处置 |
| --- | --- | --- |
| `0003-Leaves-Fakeplayer`（paper-server） | `CraftWorld.java` hunk#5 | 已由 `ps_fix_263.py` 重锚定落地 |
| `0013-fix-compat-...`（paper-server） | `PaperEventManager.java` import hunk | no-op（Timings 已延期），未落地 |
| `0013-fix-compat-...`（paper-server） | `CraftPlayer.java` 类尾 Bytebuf 块 | 已由 `ps_fix_263.py` 追加落地 |
| `0005-MC-Technical-Survival-Mode`（paper-server） | 整段涉及 `src/main/java/com/destroystokyo/paper/profile/PaperAuthenticationService.java`（该文件在 authlib 10 / Paper 重构中已删除）→ `apply_rc=1`；其余 hunk 已落地（nested commit `12249196e`） | **债务**：Technical Survival Mode 的认证服务侧集成缺失，需在 authlib 10 上重新设计 |

## 四、延期清单
- mc：`docs/deferred-26.3/DEFERRED-MC.md`（109 个）
- paper-server：`docs/deferred-26.3/DEFERRED-PAPER.md`（5 个：`Build-changes`、`Delete-Timings`、`Leaves-Server-Config-And-Command`、`Replay-Mod-API`、`Fix-SculkCatalyst-exp-skip`）
