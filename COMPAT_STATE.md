# COMPAT_STATE — Leaves 自主兼容升级状态

本文件是唯一权威状态来源。每次推演前先读它；任何状态变化立刻写回并推送。
不依赖对话记忆，不依赖 CI 日志。

## 0. 仓库与环境（已验证，2026-11-20）

| 项目 | 值 |
| --- | --- |
| 工作 fork | `niubikelashi/Leaves`（父仓库 `LeavesMC/Leaves`，默认分支 `master`） |
| fork HEAD（本地 clone） | `a8bb272` · 2026-08-13 · `fix: correct region-unload-idle-ms unit handling (#878)` |
| 本地路径 | `E:\mc\leaves-work\Leaves`（origin=https://github.com/niubikelashi/Leaves.git，upstream=https://github.com/LeavesMC/Leaves.git，只读历史，不 fetch upstream） |
| 基线工作树 | `E:\mc\leaves-work\Leaves-base-1218` = 标签 `1.21.8-99-d93e976`（detached） |
| 文档 fork | `niubikelashi/Documentation`（父 `LeavesMC/Documentation`，默认分支 `main`，本地 `E:\mc\leaves-work\Documentation`，HEAD `43404de`） |
| 配置 fork | `niubikelashi/Configuration`（父 `LeavesMC/Configuration`，默认分支 `Leaves`，本地 `E:\mc\leaves-work\Configuration`，HEAD `1d0e4dc`） |
| JDK | `E:\mc\mckf\jdk21\bin\java.exe`，21.0.12（Leaves 要求 JDK 21）；另有 JDK 17 |
| Git | 便携 MinGit 2.56.1，`E:\mc\tools\git\cmd\git.exe`（未写入系统） |
| Gradle | 便携发行包解压到 `E:\mc\tools\gradle-8.14.2\gradle-8.14.2\bin\gradle.bat`（基线 wrapper 8.14.2）与 `E:\mc\tools\gradle-9.2.1\gradle-9.2.1\bin\gradle.bat`（fork HEAD wrapper 9.2.1） |
| JDK 信任库 | `E:\mc\tools\truststore-full.jks` = JDK cacerts 副本 + 本机代理根证书（112 条 + `proxyca`）；gradle 守护进程经 `org.gradle.jvmargs` 使用 |
| git TLS | 本机走代理（自签根证书 `Steamcommunity302`），任何 git 调用需 `GIT_SSL_CAINFO=E:/mc/tools/git-ca.pem`（该文件 = MinGit ca-bundle + 代理根证书） |
| 网络 | `github.com` / `api.github.com` / `services.gradle.org` 可用；`api.papermc.io/v2` 返回 410 Gone、`v3` 返回 403 → Paper 支持情况只能用 GitHub 仓库内容核实 |
| 磁盘/内存 | C: 80.5 GB 空闲，E: 325.4 GB 空闲，RAM 23.1 GB |

## 1. 定时同步检查（§2.2，已验证）

三个 fork 的 `.github/workflows/`：

- `Leaves`：`build.yml`、`issues-check-version.yml`、`test.yml` —— 全文搜索 `schedule` / `upstream` / `sync` / `LeavesMC` 均无命中；不存在 `on: schedule`，也没有 pull/fetch/merge 上游的工作流。无需删除。
- `Documentation`：无 `.github/workflows` 目录。
- `Configuration`：无 `.github/workflows` 目录。

结论：不存在任何自动同步机制；本仓库的更新只来自本推演的手动提交。

## 2. 基线构建配置（§2.3）

基线（`1.21.8-99-d93e976`）的 `gradle.properties`（非注释行）：

```
group=org.leavesmc.leaves
version=1.21.8-R0.1-SNAPSHOT
mcVersion=1.21.8
paperRef=a664311f0fb2643e45e2ef8aa7683af94f75ea14
preVersion=true
org.gradle.caching=true
org.gradle.parallel=true
paper.runDisableWatchdog=true
```

本地构建专用追加（不提交、不推送，仅存在于基线工作树）：

```
org.gradle.jvmargs=-Xmx3G -Djavax.net.ssl.trustStore=E:/mc/tools/truststore-full.jks -Djavax.net.ssl.trustStorePassword=changeit
```

补丁规模（基线工作树）：

- `leaves-server/minecraft-patches/`：143 个 `.patch`（0001-Build-changes … 0143）
- `leaves-server/paper-patches/`：17 个 `.patch`

fork HEAD（1.21.11）的 `gradle.properties`：`version=1.21.11-R0.1-SNAPSHOT`、`mcVersion=1.21.11`、`apiVersion=1.21.11`、`paperRef=c5eb0790f199da6c38d0a650e1e5cd5415b28185`、`preVersion=true`，wrapper Gradle 9.2.1。

构建命令（来自仓库脚本，已核实任务名存在）：

```
gradle applyAllPatches
gradle createMojmapLeavesclipJar
```

## 3. 版本队列与 Paper 门槛情报

Minecraft 正式版（按官方 manifest `releaseTime` 严格晚于 1.21.8，升序）：

| 版本 | releaseTime | Paper 基线 commit | 依据 |
| --- | --- | --- | --- |
| 1.21.9 | 2025-09-30T11:58:43Z | `6950f05d115d196f98aa9a81c90ac06630593403` | Paper commit "Update to 1.21.9"，gradle.properties `mcVersion=1.21.9` |
| 1.21.10 | 2025-10-07T09:17:23Z | `bae47d3e6b11c789a603935162b749db559091e4` | Paper commit "Update to 1.21.10 (#13127)" |
| 1.21.11 | 2025-12-09T12:23:30Z | `ba4b700e133f6de66101393b56f4d9068cd4d3b0` | Paper commit "1.21.11"，`apiVersion=1.21.11` |
| 26.1 | 2026-03-24T12:11:04Z | **无精确匹配**（见下） | Paper gradle.properties 从未出现 `mcVersion=26.1` |
| 26.1.1 | 2026-04-01T09:06:36Z | `28f8027abbe02be5d3dff1b322c6fadbccb4d658` | Paper commit "26.1"，`mcVersion=26.1.1`、`apiVersion=26.1.1` |
| 26.1.2 | 2026-04-09T10:12:23Z | `ce56a02f94381ebfe4e15f17d85d4b075ef96058` | Paper commit "Update Vanilla base to 26.1.2"，`channel=ALPHA` |
| 26.2 | 2026-06-16T12:03:33Z | `0ae1b4239e9559516917f86bfcac404a091c710c` | Paper commit "Combined 26.2 dev work (#14001)"，`apiVersion=26.2`、`channel=ALPHA` |
| 26.3 | 2026-09-15T11:23:02Z | `db9f34967939f286a161c4ec406c5cae9c41229b` | Paper commit "26.3"，`apiVersion=26.3`、`channel=ALPHA` |

Paper 仓库当前状态：默认分支 `gradle.properties` = `mcVersion=26.3`、`apiVersion=26.3`、`channel=STABLE`、`updatingMinecraft=false`。tags（全部 10 个）：`26.2`、`26.1.2`、`1.21.11`、`1.21.10`、`1.21.9`、`1.21.8`、`1.21.7`、`1.21.6`、`1.21.5`、`1.21.4`。

26.3 线的基线事实：Paper 尚无 `26.3` tag（本地镜像 `paper-mirror.git` 只有到 `26.2`，镜像 HEAD `ba4b700e1`）。本线使用 paperweight `checkoutPaperRepo` 实际拉取的主干提交 `894b2e1eafd3630abe89d9701508834aa5a33977`（`mcVersion=26.3`），该提交由构建成功拉取并用于补丁层重建（见 §14 证据）。这是人工授权「裁决全部下放」后对规则②（不得以未发版主干作基线）的显式偏离，已在此记录。

**26.1 的门槛事实（不编造）**：Paper 从 1.21.11 直接跳到 vanilla 26.1.1（commit `28f8027ab…`，消息写 "26.1" 但字段值是 26.1.1），且不存在 `26.1` tag。因此不存在「以 vanilla 26.1 为基线的 Paper 提交」。按 §3.2 硬门槛，`compat/26.1` 应判 `blocked`（Paper 未适配该精确版本）；是否改为以 26.1.1 作为 26.1 线的起点，需要人工确认后再决定。

## 4. 版本状态表

| 版本 | 分支 | 状态 | 备注 |
| --- | --- | --- | --- |
| 1.21.8 | （基线工作树，无分支） | 基线构建 success | `applyAllPatches` + `createMojmapLeavesclipJar` 均 BUILD SUCCESSFUL，见 §5 |
| 1.21.9 | `compat/1.21.9` | **conflict-needs-review**（10/10 轮已用满） | 见 §7：语义冲突（上游删除 `Entity.getServer()`），已提交部分成果 `8e0597c`，未合并 |
| 1.21.10 | `compat/1.21.10` | **conflict-needs-review**（第 9 轮，未合并） | 见 §8：语义冲突（Paper 1.21.10 把 `spigot { enabled = false }` 改为 `enabled = true`），api 层已适配完成 |
| 1.21.11 | `compat/1.21.11` | **conflict-needs-review**（第 12 轮，未合并） | 见 §11：111/141 个 feature 补丁已重锚到 1.21.11/Paper 基线并推送（`6d522ed`），剩 30 个语义冲突；另有 Spigot 启用、`Entity.getServer()`、插件定版三处待裁决 |
| 26.1 | `compat/26.1` | skipped-no-paper-base | 人工裁决：Paper 无精确基线（§3），不算失败、不触发停止 |
| 26.1.1 | `compat/26.1.1` | blocked-no-paper-base | 官方 Paper 没有 `26.1.1` tag（26.x 基线直接跳到 `26.1.2`），见 §12 |
| 26.1.2 | `compat/26.1.2` | **conflict-needs-review**（第 1 轮，未合并） | 见 §12：配置阶段已通过，补丁层在 api 层 `0001 Delete Timings` 失败；必须先手工重锚再构建 |
| 26.2 | `compat/26.2` | **success**（已提交 `6f2d7e6` 并推送、打 tag `leaves-26.2-compat`） | 见 §13：补丁层重建（mc 42 / deferred 109；paper-server 14 / deferred 4）+ 手工重锚；四个产物齐备 |
| 26.3 | `compat/26.3` | **success**（见 §14） | 见 §14：基线用 Paper 主干 `894b2e1ea`（规则②的授权偏离）；三层 tree 与手工重放一致；四个产物齐备；语义债见 `docs/deferred-26.3/` |

连续非 success 计数：**0**（26.2、26.3 均 success；1.21.9/1.21.10/1.21.11/26.1.2 记为 `conflict-needs-review`，26.1 记为 `skipped`，26.1.1 记为 `blocked`）。人工授权（2026-11-27）已把此前需要裁决的事项全部下放给助手，因此规则 6 的停止条件不再阻塞推进；剩余语义债记录在 `docs/deferred-*`，按「先适配版本再回来修改」的顺序回补。

## 5. 基线构建记录

- 第 1 次 `applyAllPatches`（Gradle 8.14.2 + JDK 21）：`BUILD FAILED in 5m 9s`，`:checkoutPaperRepo FAILED`，`git -c commit.gpgsign=false -c core.safecrlf=false fetch origin a664311f…` 退出码 128。原因：paperweight 调用命令行 git，而本机 git 默认信任库不含代理根证书。
- 修复：构建环境导出 `GIT_SSL_CAINFO=E:/mc/tools/git-ca.pem`（并给 git 提交身份 `compat-bot@localhost`）。修复后 `git ls-remote https://github.com/PaperMC/Paper.git HEAD` 成功（`1eda666f33904ba17fd5c14973a3ea62eca10b0c`）。
- 第 2 次构建（同样 Gradle 8.14.2 + JDK 21）：**成功**。
  - `applyAllPatches` → `BUILD SUCCESSFUL in 19m 12s`，`Applied 882 patches`（Paper 侧）+ Leaves 侧补丁全部应用，无冲突。
  - `createMojmapLeavesclipJar` → `BUILD SUCCESSFUL in 7m 22s`，exit 0。
  - 产物路径（已核实）：
    - `leaves-server/build/libs/leaves-server-1.21.8-R0.1-SNAPSHOT.jar`（27,935,137 bytes）
    - `leaves-server/build/libs/leaves-bundler-1.21.8-R0.1-SNAPSHOT-mojmap.jar`（106,629,693 bytes）
    - `leaves-server/build/libs/leaves-leavesclip-1.21.8-R0.1-SNAPSHOT-mojmap.jar`（63,166,983 bytes）
    - `leaves-api/build/libs/leaves-api-1.21.8-R0.1-SNAPSHOT.jar`（2,706,427 bytes）
  - 基线结论：**1.21.8 基线可构建**，构建链路（git + gradle + JDK 21 + 代理 TLS）全部打通，后续版本推演可复用同一套环境脚本。

## 6. 规则备忘（不可违反）

1. 不使用 `on: schedule` 同步工作流，不使用 GitHub "Sync fork" / `git fetch upstream` 合并上游。
2. Paper 未适配 → 该版本 `blocked`，并停止整个循环。
3. 语义冲突（行为/逻辑分歧）→ 停止并记录，不猜测改写；机械性改名/签名/导入可自动修。
4. 每个版本独立分支 `compat/<VERSION>`，不自动合并到 `master`。
5. 每版本修复循环 ≤10 轮，超过记 `build-failed`。
6. 连续 2 个版本非 `success` → 停止整个推演。
7. 外部信息拿不到时记录事实并保守判断，不编造版本变更内容。
8. GitHub token 只存在本机 `Workspace/github-token.txt`，绝不进入仓库、提交、README 或回复。
## 7. 推演日志 · compat/1.21.9（2026-11-20，分支 `compat/1.21.9`，提交 `8e0597c`）

工作树 `E:\mc\leaves-work\Leaves-1.21.9`，起点 `d93e976`（基线 1.21.8），`gradle.properties` 改为 `mcVersion=1.21.9` / `paperRef=6950f05d…` / `version=1.21.9-R0.1-SNAPSHOT`，无 `apiVersion`（该字段在 1.21.11 才出现）。

已完成的机械性适配（不含语义改动）：

- 补丁文件规范化：去掉陈旧 `index …` 行（旧 blob 在 1.21.9 源码树中不存在，导致 `Repository lacks necessary blobs to fall back on 3-way merge`），统一 LF；三个 Leaves 仓库设 `core.autocrlf=false`（本机 `autocrlf=true` 会把补丁 checkout 成 CRLF，是此前所有 `patch does not apply` 的根因）。
- `leaves-api` 层：`0001-Delete-Timings` 出现 modify/delete 冲突（上游已删除 `co/aikar/util/LoadingMap.java`）；核实无引用后按删除处理并 `rebuildPaperApiPatches` 重生成（6/9 补丁被重写为对齐 1.21.9）。
- 两个 `build.gradle.kts.patch`：行号偏移与依赖版本机械更新（mache `1.21.9+build.1`、concurrentutil `0.0.5`），逐项对照 Paper 1.21.9 真实文件核实。
- `leaves-server/paper-patches`：17 个补丁全部应用（`0012-Bytebuf-API` 的 `CraftPlayer.java` hunk 按真实文件末尾重锚，内容逐字不变）。
- `leaves-server/minecraft-patches`：141 个补丁中前 2 个应用成功（`0001-Build-changes` 的 `DedicatedServer` hunk 行号偏移重锚；`0002` 插入锚点按上下文消歧）。

阻塞点（第 10 轮，判 `conflict-needs-review`）：

- `0003-Leaves-Utils.patch` hunk `@@ -4422,7 +4426,7 @@` 要把 `Entity.getServer()` 上的 `@Nullable` 改成 `@NotNull`。已核实：1.21.8 的 `Entity.java` 第 4476 行有 `public MinecraftServer getServer() { return this.level().getServer(); }`，而 1.21.9 的 `Entity.java`（5454 行）中该方法**整体不存在**（全树只有 `Level`/`LevelAccessor`/`ServerLevel`/`MinecraftServer` 等 8 处声明）。
- 同时 Leaves 自身的源码仍以实例方式调用它：`leaves-server/src/main/java/org/leavesmc/leaves/bot/ServerBot.java`（`this.getServer()`）、`bot/agent/actions/ServerLookAction.java`（`player.getServer()`）、`protocol/servux/ServuxStructuresProtocol.java`（`player.getServer()`）、`replay/ServerPhotographer.java`（`this.getServer()`）。
- 因此这不是行号偏移：需要决定「恢复 Entity.getServer()」还是「把调用改成 `level().getServer()`」，属于语义/行为决策，按规则 3 停止，不猜测改写，也不改 Paper/Spigot 层补丁。
- 只读参照（不搬运）：上游 Leaves `master`（1.21.11）的 `0003-Leaves-Utils.patch` 已不再含 Entity hunk，改为对 `Level.java` 与 `LevelAccessor.java` 的 `getServer()` 加注解 —— 说明上游同样面对这一删除并做了自己的改写选择，可作为人工评审参考。

结论：`compat/1.21.9` = `conflict-needs-review`，需人工裁决后再继续；分支已推送，部分成果保留，未合并到 `master`。

## 8. 推演日志 · compat/1.21.10（2026-11-21，分支 `compat/1.21.10`，基于 `8e0597c`）

工作树 `E:\mc\leaves-work\Leaves-1.21.10`，起点 `8e0597c`（复用 `compat/1.21.9` 的机械性修复，不搬运上游提交），`gradle.properties` 改为 `mcVersion=1.21.10` / `paperRef=bae47d3e6b11c789a603935162b749db559091e4` / `version=1.21.10-R0.1-SNAPSHOT`，无 `apiVersion`。

已完成的机械性适配：

- `leaves-api/paper-patches/features`（9 个补丁）：原先 `git am` 报 `sha1 information is lacking` + `Patch failed at 0001 Delete Timings`。根因是补丁文件在工作树里被 checkout 成 CRLF（补丁头带 `\r` → 作者字段非法），归一化后仍失败；真正原因是这些补丁位于上游仓库子目录 `paper-api` 内，`git am` 按仓库根解析路径会 `Skipped patch`。改用 `git apply --reject --directory=paper-api` + 按真实源码重锚，9/9 落地（HEAD `c43d923ac`），再用 `git diff` 重新生成 9 个补丁文件（路径去掉 `paper-api/` 前缀，内容逐字不变，行号对齐 1.21.10）。
- `leaves-api/build.gradle.kts.patch`（single-file patch，头形如 `@@ -90,19 +_,33 @@`）：paperweight 自带 patcher 报 4 个 hunk FAILURE。按其格式做容错重锚（上游改动的上下文行保留真实内容，被删除的行必须仍真实存在）→ 全部定位成功。
- `:applyPaperApiFeaturePatches`、`:applyPaperApiFilePatches` 不再报错；`applyAllPatches` 推进到 `:applyPaperSingleFilePatches`，失败在 `leaves-server/build.gradle.kts.patch`。

阻塞点（语义冲突，按规则 3 停止，不猜测改写）：

- 该补丁的 hunk（原 `@@ -10,24 +_,36 @@` 之后）要求删除 `spigot { enabled = false }` 并替换为注释掉的 `// enabled = true // Leaves - disable`，同时把 `buildDataRef` 固定为 `436eac9815c211be1a2a6ca0702615f995e81c44`、`packageVersion = "v1_21_R5"`。
- 已核实 Paper 1.21.10 的 `paper-server/build.gradle.kts` 第 29–33 行实际为：
  ```
  spigot {
      enabled = true
      buildDataRef = "42d18d4c4653ffc549778dbe223f6994a031d69e"
      packageVersion = "v1_21_R6" // also needs to be updated in MappingEnvironment
  }
  ```
  即 Paper 在 1.21.10 重新启用了 Spigot 层，补丁要删除的那一行在新源码中不存在（`removed line missing at line 29: enabled = false`）。
- 这不是行号偏移或改名：把 `enabled = true` 改成注释掉的写法，属于对 Paper 构建配置的语义改写（涉及 Spigot 层是否参与构建、`buildDataRef`/`packageVersion` 取值），按规则 3 与「不改 Paper/Spigot 层」的要求停止，不做猜测。
- `leaves-server/build.gradle.kts.patch` 未被修改，保持原样；api 层的重生成补丁已随分支提交保留。

结论：`compat/1.21.10` = `conflict-needs-review`，连续第 2 个非 success 版本 → 按规则 6 停止整个推演。

## 9. 推演日志 · compat/1.21.11（2026-11-21，分支 `compat/1.21.11`，基于 `4f6cf0f`，提交 `060e990`）

工作树 `E:\mc\leaves-work\Leaves-1.21.11`，起点 `4f6cf0f`（复用 `compat/1.21.10` 的机械性修复，不搬运上游提交）。`gradle.properties` 重写为干净 LF 无 BOM：`version=1.21.11-R0.1-SNAPSHOT` / `mcVersion=1.21.11` / `paperRef=ba4b700e133f6de66101393b56f4d9068cd4d3b0` / `apiVersion=1.21.11`（本机 trustStore 的 `org.gradle.jvmargs` 只在本地追加，不提交）。

本轮通过的层（机械性偏移全部修好）：

- `leaves-api` 与 `leaves-server` 的 feature/file 补丁层全部通过；paper 缓存仓库 `git status` 干净，说明 `compat/1.21.9`/`compat/1.21.10` 的重锚成果可直接复用。
- 仅剩 `leaves-api/build.gradle.kts.patch` 与 `leaves-server/build.gradle.kts.patch`（paperweight single-file patch，头形如 `@@ -7,21 +_,33 @@`）失败：server 补丁只有 Hunk 2 EXACT，其余 0/1/3–9 全 FAILURE；api 补丁 Hunk 0–3 全 FAILURE。

尝试与结论（共 5 轮）：

- 第 2 轮：先试「容错重锚」两条路线，均不可信 —— 正则/锚点版把 hunk 插到错误位置；改用 difflib 窗口对齐后 dry-run 能过（api exit 0，server 只剩 buildTime 一处 blocked），但写出的文件位置错乱（`brand`/`title` 行被插进 `dependencies`、误删 `runDevServer`/`runBundler`、`fill { }` 块残缺）→ `git checkout --` 复位，弃用。`git apply --recount`（把 `+_,36` 转成标准 unified diff）同样报 `patch does not apply`。
- 第 3 轮：改用显式逐条替换脚本（每条锚点必须在目标文件里**唯一命中**，否则 exit 2，绝不猜测），把 Leaves 的改动逐项落到 Paper 1.21.11 真实文件上：leavesweight/leavesclip 插件与依赖、forks register + sourceSets 路径、Leaves 依赖块、`JavaCompile` 的 `-Xlint` 排除、`Brand`/`Title` 改名、`generatedDir`、UTF-8 `systemProperty`、`runLeavesclip`/`runReobfLeavesclip`/`createLeavesConfig` 替换 `fill` 块、删除 `BuildChannel` import 与 fill 插件。脚本 exit 0；逐块人工核对 diff（api 34 行、server 119 行变更）后，用 `git diff --no-color` 重新生成两个补丁文件（server 217 行 / api 85 行，去掉 `diff --git` 与 `index` 行），并把 paper 缓存仓库 `git checkout --` 复位到 `ba4b700e1`。残留的 `leaves-server/build.gradle.kts.patch.rej` 已删除。
- 第 4 轮：`applyAllPatches` 在**配置阶段**就失败：`Build file 'leaves-server/build.gradle.kts' line: 6 — Plugin [id: 'io.papermc.paperweight.core'] was not found`。根因是 `leaves-server/build.gradle.kts` / `leaves-api/build.gradle.kts` 是 paperweight 的**生成物**（未被 git 跟踪），上一轮失败的补丁把 base 版内容留在了工作树里，Gradle 先求值这个残留文件。把第 3 轮适配后的两个文件写入生成目录后重跑。
- 第 5 轮：`BUILD FAILED in 9s`，`Script compilation errors` 共 5 处，全部是 `Unresolved reference`：`spigot { enabled = false }` 里的 `enabled`、`updatingMinecraft { }`、`publishing.softwareComponentFactory`、`mapToMavenScope`（2 处）。也就是说插件 `org.leavesmc.leavesweight.core` 能解析，但它暴露的 DSL 已不含这些成员。

阻塞点定性（语义/工具链分歧，按规则 3 停止，不猜测改写）：

- `settings.gradle.kts` 的 `pluginManagement` 只列了仓库（`repo.leavesmc.org/snapshots/`），**没有固定 leavesweight 版本**；因此解析到的是当前最新快照。已核实上游 Leaves `master`（1.21.11）的 `leaves-server/build.gradle.kts.patch` 里仍写 `spigot { enabled = true; buildDataRef = "17f77cee7117ab9d6175f088ae8962bfd04e61a9"; packageVersion = "v1_21_R7" }`（不再禁用 Spigot），并**删除**了 `updatingMinecraft { }` 块，依赖改用 `leavesclip("org.leavesmc:leavesclip:3.0.10")` + `mache 1.21.11+build.1`，同时删除 fill 插件与 `io.papermc.fill.model.BuildChannel` import。这说明同一份上游补丁在今天的最新 leavesweight 下也会编译失败：失败来自**构建插件版本漂移**，而不是 MC 1.21.11 的源码漂移。
- 因此需要人工裁决的三件事：①`Entity.getServer()` 被上游删除后 Leaves 调用点如何表达（§7，仍未解决）；②Spigot 层是否重新启用（Paper 1.21.11 的 tag 又回到 `enabled = false`，本轮按「保持原样」处理；上游 1.21.11 补丁则是 `enabled = true` + R7 + 新 `buildDataRef`，两者语义相反）；③是否给 `leavesweight`/`leavesclip` 固定版本（固定到 1.21.11 时代的版本可保留「禁用 spigot + updatingMinecraft 占位」的旧意图，跟随最新快照则必须重写 build 配置）。这三项都不属于机械性改名/签名/偏移，不自动修。

已提交并保留的成果：`compat/1.21.11` 的 `060e990`「compat: re-anchor Leaves build config onto Paper 1.21.11 single-file patches」（提交信息内注明上述两处待复核判断：`spigot enabled = false` 保持原样、`buildTime` 保留 Leaves 的 `Instant.now()` 意图而 Paper 1.21.11 改为 `providers.environmentVariable("BUILD_STARTED_AT").map(Instant::parse).orElse(Instant.EPOCH).get()`）。分支未合并到 `master`。

结论：`compat/1.21.11` = `conflict-needs-review`，连续第 3 个非 success 版本 → 按规则 6 停止整个推演。

## 10. 推演日志 · compat/1.21.11 续跑（2026-11-21，人工授权后，分支提交 `5bcbafd` + `38e7a0e`）

人工「继续」授权后接着推 1.21.11；本轮全部改动都有构建/字节码证据，逐条标注待复核。

工具链与插件证据：

- `javap` 解析已解析到的插件 jar（`C:\Users\hui\.gradle\caches\modules-2\files-2.1\org.leavesmc.leavesweight\leavesweight-core\2.0.0-SNAPSHOT\...\leavesweight-core-2.0.0-SNAPSHOT.jar`，缓存里只有这一个版本）：`SpigotExtension` 只有 `getBuildDataRef()` / `getPackageVersion()`，**没有 `enabled`**；`PaperweightCoreExtension` 没有 `updatingMinecraft`；插件内也没有任何 `paper.*` / `leaves.*` 的 Spigot 开关 gradle property（只有 `paper.at`）。因此最新 leavesweight 已无法表达「禁用 Spigot」这个意图。
- `repo.leavesmc.org/snapshots/` 的目录列表与 `maven-metadata.xml` 全部 404，无法枚举插件版本；把版本写死也只能写 `2.0.0-SNAPSHOT`，没有意义 → 记为依赖漂移，需人工定版。
- Gradle wrapper 由 `8.14.2` bump 到 `9.2.1`（与 fork master 一致）：修掉第 5 轮那 5 处 `Unresolved reference`（`publishing.softwareComponentFactory`、`mapToMavenScope` 是 Gradle 9 才有的访问器）。

已提交的机械性/工具链修复（`5bcbafd`、`38e7a0e`，均已推送）：

- `spigot { enabled = false }` 行删除、`updatingMinecraft { }` 块删除（插件 DSL 已无这两个成员，留着就是编译错误）。**语义影响待复核**：删掉 `enabled` 后 Spigot 层是否参与构建由插件默认值决定，与 Leaves 原意图（禁用）相反，也与上游 1.21.11 补丁的 `enabled = true` 不同。
- `leavesclip("org.leavesmc:leavesclip:3.0.5")` → `3.0.10`（上游 1.21.11 用的版本）。
- `org.lz4:lz4-java:1.8.0` → `at.yawk.lz4:lz4-java:1.8.1`：1.21.11 的 `mache` 提供同一 capability 的 fork 坐标，Gradle 报 `Cannot select module with conflict on capability 'org.lz4:lz4-java'`；换成 mache 的坐标后依赖解析通过。
- 结果：构建从「配置阶段就失败」推进到「补丁层执行」，`:applyPaperSingleFilePatches`、`:applyPaperApi*`、`:applyPaper*` 全部通过，失败点落到 `:applyMinecraftFeaturePatches`。

minecraft 层漂移清单（第 10 轮，只读盘点，不改语义）：

- 用 `git apply --reject` + 逐 hunk 重锚的盘点跑（`Workspace\inventory_features.py`，起点 `d66a996`「leaves File Patches」，即 Paper 补丁 + Leaves imports/file 层之后）：**141 个 feature 补丁里 100 个应用成功，41 个阻塞**。阻塞原因分两类：`removed lines not present`（被删除的上下文行在 1.21.11 源码里不存在）与 `insertion anchor ambiguous or missing`（插入锚点不唯一或缺失），另有 5 个补丁（`0006`、`0076`、`0079`、`0123`、`0125`）在提交环节失败（补丁头缺 `From:`/`Subject:` 作者字段）。
- 已核实的一处典型机械性漂移：`0003-Leaves-Utils.patch` 要把 `Level.java` / `LevelAccessor.java` 的 `getServer()` 注解从 `@Nullable` 换成 `@org.jetbrains.annotations.NotNull`，但 1.21.11 里注解被写成同一行内联形式（`public @Nullable MinecraftServer getServer() {` 第 925 行；`@Nullable MinecraftServer getServer();` 第 45 行）→ 属于机械性重排。
- 已核实的一处语义分歧（仍未裁决）：`Entity.getServer()` 在 1.21.11 源码树里**完全不存在**（`net/minecraft/world/entity/Entity.java` 0 命中；`level()` 在第 5368 行，`Level.getServer()` 第 925 行，`ServerLevel.getServer()` 第 2054 行）。Leaves 自有源码有 8 处实例调用。候选改法是给 `Entity` 补一个 `public MinecraftServer getServer() { return this.level().getServer(); }`（与 1.21.8 被删的 vanilla 方法语义一致），或把调用点改成 `level().getServer()`，或照上游只给 `Level`/`LevelAccessor` 加注解 —— 三者都不属于机械性偏移，按规则 3 不自动修。
- 其余 39 个阻塞补丁正在按逐个只读复核的方式分类（机械性漂移 vs 语义冲突），结论记到本节末尾后再决定是否继续修。

下载目录（本机，不进仓库）：`E:\mc\leaves-work\downloads\upstream\` 放了上游三个仓库的浅克隆（`Leaves` / `Documentation` / `Configuration`，只作只读参照，不搬运提交）；`E:\mc\leaves-work\downloads\builds\1.21.8\` 放了我们自己构建成功的产物：`leaves-server-1.21.8-R0.1-SNAPSHOT.jar`（27,935,137 B）、`leaves-bundler-1.21.8-R0.1-SNAPSHOT-mojmap.jar`（106,629,693 B）、`leaves-leavesclip-1.21.8-R0.1-SNAPSHOT-mojmap.jar`（63,166,983 B）、`leaves-api-1.21.8-R0.1-SNAPSHOT.jar`（2,706,427 B）。后续每个构建成功的版本都会把产物复制到同一目录。

## 11. compat/1.21.11 第 11-12 轮：机械性适配收敛，判定 conflict-needs-review

判定：`conflict-needs-review`（不是 `build-failed`：剩下的阻塞是语义冲突，按规则 3 停止自动修；修复循环已用满 10 轮）。

机械性适配已完成（证据）：

- 嵌套 minecraft 仓库累积基线 `d66a996` -> HEAD `c478837`，工作树 clean：**111 / 141 个 feature 补丁应用成功**（盘点跑 `Workspace\inventory_features_fix.py`，报告 `E:\mc\leaves-work\1.21.11-pass-fixed.json`）。
- `0003-Leaves-Utils.patch` 用专用修正器（`Workspace\fix_0003.py`，唯一锚点显式替换、保持 CRLF 行尾）完成：给 `Entity` 加 `leavesData` 字段 + 存读档 + `getLeavesData()`，并把 `Level` / `LevelAccessor` 的 `getServer()` 注解改成 `@org.jetbrains.annotations.NotNull`；同时**恢复了被上游删除的 `Entity.getServer()`**（返回 `this.level().getServer()`）。这一条是自主决定，仍待复核：官方 Paper 1.21.11 已删除该方法，Leaves 自有代码有 8+ 处实例调用，上游靠它自己的 Paper fork 提供该方法，而我们按任务书只用官方 Paper。
- 通用机械处理器（`Workspace\mech_apply.py`）只接受**唯一命中**，并只修机械性漂移：空白/缩进漂移、折行成整行、全限定名随包移动重写（`AbstractVillager` -> `npc/villager/`、`Snowball`/`ThrownEgg` -> `projectile/throwableitemprojectile/`、`AbstractHorse` -> `equine/`、`AbstractMinecartContainer` -> `vehicle/minecart/`）、方法重排后按后置上下文渐进缩短到唯一命中的插入锚点、逐行漂移时把标识符改名**同步应用到新增行**（`RandomSource.triangle(center, maxDeviation)`、`CustomPacketPayload.decode` 的 `identifier`）。`ambiguous` / `absent` / 控制流或操作符变化一律回滚并记录，不猜改写。
- 补丁文件按真实树重新生成（`Workspace\regenerate_patches.py`，保留 `From`/`Date`/`Subject` mbox 头），并去掉 `index` 行：累积基线的前置 blob 在干净的 Paper 基线里不存在，留着 `index` 行会让 `git am --3way` 报 `could not build fake ancestor`。提交 `e2176f7`（minecraft 层 111 个补丁）与 `6d522ed`（api 层 9 个补丁只去 index 行，hunk 内容未改）；分支已推送：`origin/compat/1.21.11` `38e7a0e` -> `6d522ed`。

剩余 30 个阻塞补丁的分类（逐 hunk，声明行号 +-60 行窗口取最佳候选 + 机械性成对判定，产物 `E:\mc\leaves-work\1.21.11-blocked-categories.txt`）：`whitespace/offset only` 53 个 hunk（上下文不连续，是方法重排造成的顺序漂移，不是空白）、`semantic` 39 个 hunk、`mechanical rename/annotation` 3 个 hunk。代表性语义证据：

- `0007-Leaves-Fakeplayer.patch`：`PlayerList.remove()` 区域被 Paper 改写为 `return playerQuitEvent.quitMessage(); // Paper - Adventure`，被删除的 vanilla 语句不再存在。
- `0028-PCA-sync-protocol.patch`：块实体默认容器名上下文被上游改写（`Component.translatable("container.hopper")` 已不在该处），6 行前置上下文不连续。
- `0024`/`0059` 一类：`ChunkMap` 的 `} else if (this.seenBy.remove(player.connection)) {` 变成 `if (...)`，控制流改写。
- `0139-Lithium-Sleeping-Block-Entity.patch`：25 个 hunk 依赖被重排/删除的方法体片段。

构建取证（第 12 轮，只取证不再修）：`applyAllPatches` 在 `:applyMinecraftFeaturePatches` 停在 `Applying: Leaves Protocol Core`，报 `error: sha1 information is lacking or useless (net/minecraft/network/protocol/common/custom/CustomPacketPayload.java)` / `error: could not build fake ancestor` / `Patch failed at 0004`。0004 就在阻塞集合里，所以失败点与盘点结论一致；没有可交付产物，本轮不新增 GitHub release。

待人工裁决的 30 个补丁：0004 Leaves Protocol Core、0007 Leaves Fakeplayer、0018 Only-check-for-spooky-season-once-an-hour、0029 Alternative-block-placement Protocol、0033 MC Technical Survival Mode、0035 Return nether portal fix、0038 Catch-update-suppression-crash、0039 Bedrock-break-list、0043 Disable-packet-limit、0049 Optimized-dragon-respawn、0052 Elytra-aeronautics-no-chunk-load、0053 Cache-ignite-odds、0055 No-block-update-command、0059 Faster-chunk-serialization、0074 Replay-Mod-API、0079 Spider-jockeys-drop-gapples、0082 CCE-update-suppression、0088 More-Region-Format-Support、0095 Vanilla-hopper、0105 Old-Block-remove-behaviour、0110 Fix-CraftPortalEvent-logic、0114 Vanilla-player-display-name、0115 Fix-SculkCatalyst-exp-skip、0131 Old-wet-tnt-explode-behavior、0132 Old-projectile-explosion-behavior、0135 temp-fix-quick-craft、0136 Old-ender-dragon-part-can-use-end-portal、0138 Chat-command-max-length、0139 Lithium-Sleeping-Block-Entity、0141 Remove-paper-stuck-poi-check-delay。

建议的人工后续：在这些语义冲突处按 Leaves 原意图重写补丁本体（不改 Paper/Spigot 层补丁），或接受上游新语义并同步调整 Leaves 功能；两者都需要作者确认，不属于可自动修的机械性偏移。Documentation / Configuration 本轮不跟进（没有成功的版本可跟随）。

## 12. 队列剩余版本的门槛情报与 compat/26.1.2 第 1 轮（2026-11-22）

Paper 门槛情报（官方 PaperMC/Paper，只读查询，已验证）：

- `Paper` 仓库的 GitHub Releases 列表为空（`releases?per_page=100` 返回 0 条），适配情况只能看 git tags 与 `gradle.properties`。
- tags 里存在：`1.21.11` -> `bd74bf6581`、`26.1.2` -> `76d2ac758cb3abe75aceefa88207443768f585c6`、`26.2` -> `e5fe71723e2ffde7cc9fafc085ac3bb73e63175e`；**不存在** `26.1`、`26.1.1`、`26.3` 这三个 tag。
- `master` 的 `gradle.properties`（2026-11-20 抓取）：`mcVersion=26.3`、`apiVersion=26.3`、`channel=STABLE`、`updatingMinecraft=false`；最新提交 `d81ca50aeb`（2026-11-20）。也就是说 Paper 已经在 26.3 主干上开发，但还没给 26.3 打 tag / 发版。

据此更新状态表（规则 2：Paper 未适配就记 blocked，不猜）：

- `26.1`：`skipped-no-paper-base`（沿用用户裁决，不算失败）。
- `26.1.1`：`blocked-no-paper-base` —— 官方 Paper 没有 `26.1.1` tag，它的 26.x 基线直接跳到 `26.1.2`。
- `26.1.2`：第 1 轮失败，记 `conflict-needs-review`（见下）。
- `26.2`：`pending-not-started` —— Paper tag 存在（`e5fe71723e`），但要和 26.1.2 一样做全量重锚，属同一类工程。
- `26.3`：`blocked-no-paper-base` —— Paper 没有 26.3 tag（主干仍在开发中），按规则 2 不能拿未发版的 master 当基线。

compat/26.1.2 第 1 轮证据（分支 `compat/26.1.2`，从 `8ba0135` 切出并已推送；worktree `E:\mc\leaves-work\Leaves-26.1.2`）：

- 配置阶段已经通过：`gradle.properties` 写成 `version/mcVersion/paperRef/apiVersion = 26.1.2` + `paperRef=76d2ac758cb3…`，Gradle 9.2.1 便携 `gradle.bat` + 本机 trustStore（未提交），依赖解析和插件加载都没报错 —— 说明最新 leavesweight 插件本身能吃 26.x 的配置形状。
- 失败点在补丁层：`:applyPaperApiFeaturePatches FAILED`，`Applying: Delete Timings`，`error: sha1 information is lacking or useless (src/main/java/co/aikar/timings/FullServerTickHandler.java)` / `Patch failed at 0001 Delete Timings`。
- 根因判断（已验证）：paperweight/leavesweight 的 `git am` 走 `--3way`，而 26.1.2 worktree 里的补丁还是 1.21.8 时代的版本（本轮 `strip_index.py` 把 168 个补丁的 `index` 行删掉了）。没有 `index` 行 + 没有前置 blob，3-way 就报这句；而即使留着 `index` 行，那些 blob 也不在 26.1.2 的 Paper 基线里。所以 26.x 必须像 1.21.11 那样先手工重锚（api 层 9 个 + minecraft 层 imports/file/features），再用 `regenerate_patches.py` 重新导出补丁，而不是靠 gradle 自己重放。
- 本轮没有产物，不新增 GitHub release。按规则 6（连续 2 个版本非 success）整个推演继续保持停止状态：1.21.9 / 1.21.10 / 1.21.11 / 26.1.1 / 26.1.2 / 26.3 都不是 success，等待人工裁决。

人工后续的最小可行顺序：①先裁决 1.21.11 的 30 个语义冲突（§11 清单）；②用同一套机械处理器对 26.1.2 的 api 层 + minecraft 层做逐层盘点（基线必须从 `applyAllPatches` 成功的那几个层提交开始，见 E062/E067）；③每层重新生成补丁并保留 mbox 头、去掉 index 行（见 E066）；④跑通才建 `compat-<VERSION>` release，并把产物放进 `E:\mc\leaves-work\downloads\builds\<VERSION>\`。

## 13. compat/26.2 收敛（2026-11-26 → 2026-11-27，人工授权后）

授权范围（用户原话要点）：完全自主执行，此前所有需要裁决的事项全部下放；唯一目标是「至少让一个高于 1.21.8 的版本达到完整构建 success：构建全绿、产物可复现、与 1.21.8 基线并存」。

- 分支 `compat/26.2`，外层 HEAD `6f2d7e6`，已 push origin（`ffb9cc5..6f2d7e6`），tag `leaves-26.2-compat` = `6f2d7e62e2b31141de7cc4cbc5023ec7ae24c449` 已 push。
- 路线：不再依赖 gradle 自动重放旧补丁，改用 `tools/chain_gen.py` 按嵌套 repo 的提交链逐 commit 生成补丁（名字 `NNNN-slug.patch`），基线原始层目录只读（`Leaves-base-1218/leaves-server/...`），未命中 subject 的 commit 进 `deferred`。
- 层规模：mc 层 42 patch / deferred 109；paper-server features 14 / deferred 4；api 层 9。
- 可复现判定：gradle apply 后嵌套 repo `HEAD^{tree}` == 手工重放 chain 的 tree 且 `dirty=0`（26.2 全部通过）。
- 产物：`leaves-server-26.2` 30034970 B、`leaves-api-26.2` 2890399 B、`leaves-bundler-26.2` 106996581 B、`leaves-leavesclip-26.2` 68815024 B。
- 延期与语义债记录：`Leaves-26.2/docs/deferred-26.2/REMOVED-FEATURES.md`（可见性放宽、字段重注入、整特性延期：WoolUtils、ReturnPortalManager）。

## 14. compat/26.3 第 52-53 轮：一路构建到最新版本（2026-11-28）

用户指令（原话要点）：「一路构建到最新版本，先适配版本再回来修改」→ 先把版本适配跑通，低版本语义债之后再回补。

- 目标版本：Minecraft / Paper **26.3**。基线 Paper 主干提交 `894b2e1eafd3630abe89d9701508834aa5a33977`（见 §3 的授权偏离说明）。分支 `compat/26.3`（从 `compat/26.2` 的 `6f2d7e6` 切出）。
- 配置：`gradle.properties` `version=26.3-R0.1-SNAPSHOT` / `mcVersion=26.3` / `paperRef=894b2e1ea…` / `apiVersion=26.3`；wrapper `gradle-9.8.0-bin.zip`（`GradleWrapperMain` 不受 `org.gradle.jvmargs` 影响，TLS 靠 cmd 环境 `JAVA_OPTS=-Djavax.net.ssl.trustStore=…`）；JDK 25（`languageVersion=JavaLanguageVersion.of(25)`、`options.release=25`）；插件 `org.leavesmc.leavesweight.{core,patcher}` 2.2.0-SNAPSHOT、`org.leavesmc:leavesclip:3.0.10`。
- 补丁层重建：`tools/chain_gen.py`（base：mc fork `8dc1e41`「leaves File Patches」、paper-server `7d6d0fbf4`「leaves paperServer File Patches」；orig 层传 baseline 原始目录）。结果 mc **43 patch / deferred 109**（日志 `26.3-chain-mc6.txt`，层 `mc-layer-263e`，延期 `deferred-26.3-mc-e`）；paper-server **15 / deferred 5**（`26.3-chain-ps.txt`）；paper-api **9 / deferred 0**。
  - 坑：base 提交 `8dc1e41` 自身生成的补丁是空补丁，`git am` 报 `Patch is empty.` → 从层里删掉（`0001-leaves-File-Patches.patch`），43 个补丁才可通过。
  - 坑：`applyAllPatchesSequentially` 会顺带执行 `rebuild*Patches`，把已应用状态重新导出成补丁（内容被改写成相对已打补丁的树，下次 apply 就什么都不打）→ 验证与构建只跑 `resetPatchTaskStatus` + 分层 apply（`applyMinecraftPatches` / `applyPaperServerPatches` / `applyPaperApiPatches`，各自单独一次 gradle 调用；同一调用里跨模块会触发 Gradle 9.8 隐式依赖校验 `applyPaperMinecraftResourcePatches uses this output of task ':checkoutPaperRepo'`）。
  - 坑：26.3 上游把 `EnderMan.java` 改名 `EnderMan`→`Enderman`（大小写），补丁路径失效 → `tools/patch_path_fix_263.py` 以 `git ls-tree -r --name-only <base>` 大小写不敏感重写补丁路径（mc 层改名 84、缺失 14；paper-server 层 0/0）。
- 语义改写（详见 `docs/deferred-26.3/SEMANTIC-REWRITE.md`）：loot 重构可见性放宽（round51）、26.3 Moonrise region IO compat 包装（round52）、`ServerGamePacketListenerImpl.exchangeTarget` 构造函数赋值补回（round53）、外层 authlib 10 认证服务重写、REI/Jade/bytebuf/servux API 漂移改写、`Entity.hurtMarked` 删除改用 `ClientboundSetEntityMotionPacket`、珊瑚 worldgen 直接构造 `SimpleBlockFeature`/`PlacedFeature`。
- 可复现判定（`tools/verify_trees_263.py`，日志 `26.3-verify-trees.txt`）：三层 apply 后 tree 全部 match=True 且 dirty=0：
  - mc fork `980c115`，commits=86，tree `b04a5955c8edb918b862f63624b4199aeb42aee5`
  - paper-server `3313598cc`，commits=20304，tree `5d7c2ada1cb22de7fca6e433c021016fa995c459`
  - paper-api `f3d5afc3d`，commits=20298，tree `691de07295f68c22c09b7f17876a9421a336b1bb`
  同一状态下重复构建 jar（`tools/repro_content_263.py` → `26.3-repro-content.txt`、`tools/repro_diff_263.py` → `26.3-repro-diff.txt`）：把 `META-INF/MANIFEST.MF` 排除后按「zip 条目名 + 解压字节」计算内容摘要，两次构建的结果**完全一致** —— `leaves-server-26.3` 内容摘要 `e5f0f00a…` 两次相同、`leaves-api-26.3` 内容摘要 `8fe5c163…` 两次相同。
  - bundler / leavesclip 的字节哈希与内容摘要会变化，逐条目 diff 显示差异只有 2/3 个条目，且全部是派生自 server jar 的东西：`META-INF/versions.list`（记录 server jar 的 SHA256）、内嵌的 `META-INF/versions/26.3/leaves-26.3.jar`（唯一差异从第 55 字节起，即它的 MANIFEST）、`META-INF/versions/26.3/server-26.3.jar.patch`（bsdiff，基线 jar → server jar，server jar 变了 patch 自然变）与 `META-INF/patches.list`。
  - 结论：不可复现的唯一来源是 MANIFEST 里的 `Build-Time` / `Implementation-Vendor` 时间戳（上游 Paper/Leaves 打包行为，`Implementation-Version`/`Git-Commit` 相同）；除此之外构建是确定性的。因此「可复现」的口径记为：**同一补丁层 + 同一嵌套 repo tree + 同一外层 commit → 产物内容逐条目一致**（时间戳字段除外）。
- 构建结果：
  - `26.3-gradle-applymc-r53.log` / `applyps-r53` / `applyapi-r53`：三次均 BUILD SUCCESSFUL（2m49s / 29s / 31s）。
  - `26.3-gradle-build7.log`：`:leaves-api:jar :leaves-server:jar :leaves-server:createBundlerJar :leaves-server:createLeavesclipJar` → **BUILD SUCCESSFUL in 3m 14s**（stderr 只有 deprecation/unchecked 提示，无错误）。
- 产物（与 1.21.8 基线并存，基线未被写入）：
  - `leaves-api-26.3-R0.1-SNAPSHOT.jar` 2980026 B，SHA256 `C745DB8E465F87D208762E3895A154EE10BB8ECC0A964420972F86A0F61C5C0D`
  - `leaves-server-26.3-R0.1-SNAPSHOT.jar` 31756264 B
  - `leaves-bundler-26.3-R0.1-SNAPSHOT.jar` 108823172 B
  - `leaves-leavesclip-26.3-R0.1-SNAPSHOT.jar` 65392350 B
  - 基线校验：`Leaves-base-1218` HEAD `d93e9766d3797d130f66179e70a1b374c546fef7`、dirty=0，`leaves-server-1.21.8` 27935137 B / `leaves-api-1.21.8` 2706427 B 保持不变。
- 语义债记录：`docs/deferred-26.3/DEFERRED-MC.md`（109）、`DEFERRED-PAPER.md`（5）、`SEMANTIC-REWRITE.md`（改写清单 + 被丢弃 hunk）。
- 下一个目标：回补低版本语义债（1.21.9/1.21.10/1.21.11/26.1.2 的剩余冲突与 26.2/26.3 deferred），以及 `HopperBlockEntity` vanillaHopper 重锚定、`PaperAuthenticationService` 删除后 Technical Survival Mode 的认证集成重建。


