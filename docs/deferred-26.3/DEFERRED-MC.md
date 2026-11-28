# 26.3 延期补丁（leaves-server/minecraft-patches/features）

共 109 个补丁未能在 26.3 上重放（保存在工作区 `deferred` 目录，未安装进补丁层）。

| 补丁文件 | 原始 Subject |
| --- | --- |
| `0002-Leaves-Server-Config-And-Command.patch` | Leaves Server Config And Command |
| `0003-Leaves-Utils.patch` | Leaves Utils |
| `0004-Leaves-Protocol-Core.patch` | Leaves Protocol Core |
| `0005-Configurable-trading-with-the-void.patch` | Configurable trading with the void |
| `0006-Make-snowball-and-egg-can-knockback-player.patch` | Make snowball and egg can knockback player |
| `0007-Leaves-Fakeplayer.patch` | Leaves Fakeplayer |
| `0008-Make-shears-in-dispenser-can-unlimited-use.patch` | Make shears in dispenser can unlimited use |
| `0010-Add-isShrink-to-EntityResurrectEvent.patch` | Add isShrink to EntityResurrectEvent |
| `0011-Movable-Budding-Amethyst.patch` | Movable Budding Amethyst |
| `0013-Stick-can-change-ArmorStand-arm-status.patch` | Stick can change ArmorStand arm status |
| `0014-Configurable-MC-59471.patch` | Configurable MC-59471 |
| `0015-No-chat-sign.patch` | No chat sign |
| `0016-Dont-send-useless-entity-packets.patch` | Dont send useless entity packets |
| `0017-Optimize-suffocation.patch` | Optimize suffocation |
| `0018-Only-check-for-spooky-season-once-an-hour.patch` | Only check for spooky season once an hour |
| `0019-Config-to-disable-method-profiler.patch` | Config to disable method profiler |
| `0020-Throttle-goal-selector-during-inactive-ticking.patch` | Throttle goal selector during inactive ticking |
| `0021-Reduce-entity-allocations.patch` | Reduce entity allocations |
| `0022-Remove-lambda-from-ticking-guard.patch` | Remove lambda from ticking guard |
| `0023-Cache-climbing-check-for-activation.patch` | Cache climbing check for activation |
| `0024-Reduce-chunk-loading-lookups.patch` | Reduce chunk loading & lookups |
| `0025-InstantBlockUpdater-Reintroduced.patch` | InstantBlockUpdater Reintroduced |
| `0026-Random-flatten-triangular-distribution.patch` | Random flatten triangular distribution |
| `0028-PCA-sync-protocol.patch` | PCA sync protocol |
| `0029-Alternative-block-placement-Protocol.patch` | Alternative block placement Protocol |
| `0030-Jade-Protocol.patch` | Jade Protocol |
| `0033-MC-Technical-Survival-Mode.patch` | MC Technical Survival Mode |
| `0034-Item-overstack-util.patch` | Item overstack util |
| `0035-Return-nether-portal-fix.patch` | Return nether portal fix |
| `0036-Leaves-Extra-Yggdrasil-Service.patch` | Leaves Extra Yggdrasil Service |
| `0037-Configurable-vanilla-random.patch` | Configurable vanilla random |
| `0038-Catch-update-suppression-crash.patch` | Catch update suppression crash |
| `0039-Bedrock-break-list.patch` | Bedrock break list |
| `0040-Fix-trapdoor-feature.patch` | Fix trapdoor feature |
| `0041-Disable-distance-check-for-UseItemOnPacket.patch` | Disable distance check for UseItemOnPacket |
| `0042-No-feather-falling-trample.patch` | No feather falling trample |
| `0043-Disable-packet-limit.patch` | Disable packet limit |
| `0044-Syncmatica-Protocol.patch` | Syncmatica Protocol |
| `0045-Shared-villager-discounts.patch` | Shared villager discounts |
| `0046-Redstone-ignore-upwards-update.patch` | Redstone ignore upwards update |
| `0047-Disable-check-out-of-order-command.patch` | Disable check out-of-order command |
| `0049-Optimized-dragon-respawn.patch` | Optimized dragon respawn |
| `0050-Creative-fly-no-clip.patch` | Creative fly no clip |
| `0052-Elytra-aeronautics-no-chunk-load.patch` | Elytra aeronautics no chunk load |
| `0053-Cache-ignite-odds.patch` | Cache ignite odds |
| `0054-Lava-riptide.patch` | Lava riptide |
| `0055-No-block-update-command.patch` | No block update command |
| `0056-Raider-die-skip-self-raid-check.patch` | Raider die skip self raid check |
| `0057-Container-open-passthrough.patch` | Container open passthrough |
| `0059-Faster-chunk-serialization.patch` | Faster chunk serialization |
| `0060-Skip-secondary-POI-sensor-if-absent.patch` | Skip secondary POI sensor if absent |
| `0061-Store-mob-counts-in-an-array.patch` | Store mob counts in an array |
| `0062-Optimize-noise-generation.patch` | Optimize noise generation |
| `0063-Optimize-sun-burn-tick.patch` | Optimize sun burn tick |
| `0064-Reduce-lambda-and-Optional-allocation-in-EntityBased.patch` | Reduce lambda and Optional allocation in |
| `0065-Avoid-Class-isAssignableFrom-call-in-ClassInstanceMu.patch` | Avoid Class#isAssignableFrom call in ClassInstanceMultiMap |
| `0066-Optimized-CubePointRange.patch` | Optimized CubePointRange |
| `0067-Check-frozen-ticks-before-landing-block.patch` | Check frozen ticks before landing block |
| `0070-Fix-villagers-dont-release-memory.patch` | Fix villagers dont release memory |
| `0071-Avoid-anvil-too-expensive.patch` | Avoid anvil too expensive |
| `0074-Replay-Mod-API.patch` | Replay Mod API |
| `0075-Leaves-I18n-support.patch` | Leaves I18n support |
| `0077-RNG-Fishing.patch` | RNG Fishing |
| `0078-Wool-Hopper-Counter.patch` | Wool Hopper Counter |
| `0080-Force-Void-Trade.patch` | Force Void Trade |
| `0082-CCE-update-suppression.patch` | CCE update suppression |
| `0083-Armor-stand-cant-kill-by-mob-projectile.patch` | Armor stand cant kill by mob projectile |
| `0086-Copper-Bulb-1-gt-delay.patch` | Copper Bulb 1 gt delay |
| `0087-Crafter-1-gt-delay.patch` | Crafter 1 gt delay |
| `0088-More-Region-Format-Support.patch` | More Region Format Support |
| `0091-Placing-locked-hopper-no-longer-send-NC-updates.patch` | Placing locked hopper no longer send NC updates |
| `0092-Renewable-deepslate.patch` | Renewable deepslate |
| `0093-Renewable-sponges.patch` | Renewable sponges |
| `0094-Renewable-coral.patch` | Renewable coral |
| `0096-Old-hopper-suckin-behavior.patch` | Old hopper suckin behavior |
| `0097-Fix-falling-block-s-block-location.patch` | Fix falling block's block location |
| `0098-Bytebuf-API.patch` | Bytebuf API |
| `0099-Allow-grindstone-overstacking.patch` | Allow grindstone overstacking |
| `0101-Disable-end-gateway-portal-entity-ticking.patch` | Disable end gateway portal entity ticking |
| `0102-Disable-crystal-portal-proximity-check.patch` | Disable crystal-portal proximity check |
| `0104-Fix-FallingBlockEntity-Duplicate.patch` | Fix FallingBlockEntity Duplicate |
| `0105-Old-Block-remove-behaviour.patch` | Old Block remove behaviour |
| `0106-Old-raid-behavior.patch` | Old raid behavior |
| `0107-Allow-anvil-destroy-item-entities.patch` | Allow anvil destroy item entities |
| `0108-Disable-vault-blacklist.patch` | Disable vault blacklist |
| `0109-Fix-EntityPortalExitEvent-logic.patch` | Fix EntityPortalExitEvent logic |
| `0110-Fix-CraftPortalEvent-logic.patch` | Fix CraftPortalEvent logic |
| `0112-Skip-negligible-planar-movement-multiplication.patch` | Skip negligible planar movement multiplication |
| `0114-Vanilla-player-display-name.patch` | Vanilla player display name |
| `0115-Fix-SculkCatalyst-exp-skip.patch` | Fix SculkCatalyst exp skip |
| `0119-Do-not-reset-placed-block-on-exception.patch` | Do not reset placed block on exception |
| `0120-Do-not-prevent-block-entity-and-entity-crash-at-Leve.patch` | Do not prevent block entity and entity crash at LevelChunk |
| `0121-Tripwire-behavior-modifier.patch` | Tripwire behavior modifier |
| `0123-Old-zombie-reinforcement.patch` | Old zombie reinforcement |
| `0124-Sound-update-suppression.patch` | Sound update suppression |
| `0126-Fast-exp-orb-absorb.patch` | Fast exp orb absorb |
| `0127-Vanilla-Fluid-Pushing.patch` | Vanilla Fluid Pushing |
| `0128-Configurable-item-damage-check.patch` | Configurable item damage check |
| `0129-Old-Throwable-Projectile-tick-order.patch` | Old Throwable Projectile tick order |
| `0130-Old-leash-behavior-when-use-firework.patch` | Old leash behavior when use firework |
| `0132-Old-projectile-explosion-behavior.patch` | Old projectile explosion behavior |
| `0133-Prevent-loss-of-item-drops-due-to-update-suppression.patch` | Prevent loss of item drops due to update suppression when |
| `0134-Fix-block-place-desync-due-to-update-suppression.patch` | Fix block place desync due to update suppression |
| `0135-temp-fix-quick-craft.patch` | temp fix quick craft |
| `0136-Old-ender-dragon-part-can-use-end-portal.patch` | Old ender dragon part can use end portal |
| `0138-Chat-command-max-length.patch` | Chat command max length |
| `0139-Lithium-Sleeping-Block-Entity.patch` | Lithium Sleeping Block Entity |
| `0140-Bring-back-LivingEntity-effect-CME.patch` | Bring back LivingEntity effect CME |
| `0141-Remove-paper-stuck-poi-check-delay.patch` | Remove paper stuck poi check delay |
