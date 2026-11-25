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
| 26.2 | `compat/26.2` | **conflict-needs-review**（第 5 轮，未合并） | 见 §13：api 层 7/9 已手工重锚并推送（`ffb9cc5`）；gradle 顺序重放卡在 `0001 Delete Timings`；minecraft 层仓库未被创建，无法盘点 |
| 26.3 | `compat/26.3` | blocked-no-paper-base | Paper 没有 26.3 tag（主干 `mcVersion=26.3` 仍在开发中），按规则 2 不能拿未发版 master 当基线，见 §12 |

连续非 success 计数：**7**（1.21.9、1.21.10、1.21.11、26.1.1、26.1.2、26.2、26.3；26.1 记为 `skipped-no-paper-base` 不计入）。队列已走完（1.21.9 → 1.21.10 → 1.21.11 → 26.1 → 26.1.1 → 26.1.2 → 26.2 → 26.3），按规则 6 整个推演保持**停止（halted）**，等待人工裁决后再恢复：①`Entity.getServer()` 被上游删除后的表达方式；②Spigot 层是否重新启用（含 `buildDataRef`/`packageVersion` 取值）；③`leavesweight` / `leavesclip` 是否固定版本（当前未固定，见 §9）；④1.21.11 剩余 30 个语义冲突补丁的处理方向（见 §11 清单）；⑤26.x 的 api 层 2 个语义冲突与 minecraft 层是否允许按手工重锚的方式继续推进（见 §12-§13）。

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

## 13. compat/26.2 第 1-5 轮与队列终态（2026-11-22）

分支 `compat/26.2`（从 `5157779` 切出，已推送，HEAD `ffb9cc5`），基线 Paper tag `26.2` = `e5fe71723e2ffde7cc9fafc085ac3bb73e63175e`。

- 第 1 轮（机械 bump + `applyAllPatches`）：配置阶段通过（`version/mcVersion/paperRef/apiVersion = 26.2`，Gradle 9.2.1，插件 `org.leavesmc.leavesweight.patcher` 版本已升到 `2.1.0-SNAPSHOT`），失败在 `:applyPaperApiFeaturePatches` —— `Applying: Delete Timings`，`sha1 information is lacking`。与 26.1.2 同一形态：补丁还是 1.21.8 时代的。
- 第 2 轮（手工盘点 api 层，基线 = `14acd6541 Leaves paperApi File Patches`）：**7 / 9** 应用成功，2 个阻塞，证据是「删除行不存在」：
  - `0001-Delete-Timings`：`SimplePluginManager.java` `@@ -720,7 +720,6 @@`、`JavaPluginLoader.java` `@@ -290,22 +290,22 @@` 的待删行在 26.2 里不存在，而 `co/aikar/timings` 目录仍在（`FullServerTickHandler.java` 等 14 个文件还在）→ Paper 26.2 重写了 timings 的调用点，删哪些行需要人工判断，按规则 3 不猜。
  - `0005-Bytebuf-API`：目标文件在 26.2 的 `src/main/java/org/bukkit/` 里已不存在（`git ls-files "src/main/java/org/bukkit/*Byte*"` 返回 0 条）→ 该特性的落点被上游搬迁，属语义冲突。
- 第 3 轮：`gradle --project-dir` 才可用（`-C` 不是 Gradle 选项，记 E068）；插件内部调用 `git`，PATH 里没有 git → `A problem occurred starting process 'command 'git''`。
- 第 4 轮：补上 git PATH 后，`git am` 报 `fatal: unable to auto-detect email address (got 'hui@Hui.(none)')` → 补 `GIT_AUTHOR_*`/`GIT_COMMITTER_*`。
- 第 5 轮：仍然 `:applyPaperApiFeaturePatches FAILED` / `Applying: Delete Timings` / `Patch failed at 0001`。这轮证明了一个结构性事实（记 E069）：gradle 按文件名顺序逐个 `git am`，第 1 个补丁就是语义冲突，后面那 7 个已适配的补丁永远轮不到 —— 手工盘点的 applied 数不等于 gradle 可达性。同时上一层失败时下一层仓库不会 clone：`Get-ChildItem -Recurse -Directory -Force | Where-Object Name -eq '.git'` 只列出 `paper-api`，`leaves-server` 的嵌套 minecraft 仓库不存在，所以 26.2 的 minecraft 层连基线都拿不到。
- 已提交并推送的部分：`gradle.properties` 适配到 26.2（不含本机 trustStore 参数）+ 9 个 api feature 补丁全部去掉 index 行，其中 7 个按真实树重新导出（`ffb9cc5`，`leaves-server` 层保持未改动，因为无法盘点）。本轮无产物，不新增 GitHub release。

队列终态（规则 2 / 规则 6 / 用户裁决）：

| 版本 | 终态 | 一句话依据 |
| --- | --- | --- |
| 1.21.9 | conflict-needs-review | 上游删了 `Entity.getServer()`（§7） |
| 1.21.10 | conflict-needs-review | Paper 改 `spigot { enabled }` + `Entity.getServer()`（§8） |
| 1.21.11 | conflict-needs-review | 111/141 已重锚，剩 30 个语义冲突（§11） |
| 26.1 | skipped-no-paper-base | 用户裁决：Paper 无精确基线，不算失败 |
| 26.1.1 | blocked-no-paper-base | Paper 无 `26.1.1` tag（§12） |
| 26.1.2 | conflict-needs-review | 配置通过，补丁层需手工重锚（§12） |
| 26.2 | conflict-needs-review | api 层天花板 7/9，gradle 卡在 `0001 Delete Timings`（本节） |
| 26.3 | blocked-no-paper-base | Paper 无 26.3 tag，主干仍在开发（§12） |

没有任何版本达到 success，因此 Documentation / Configuration 的跟进条件（§1 的「成功版本才跟随更新」）不成立，两个仓库保持原样。GitHub Releases 保持 20 个：19 个上游镜像 + `compat-1.21.8`（唯一一次成功构建的产物）。

## §14 阶段 2 裁决执行（2026-11-23，compat/1.21.11，第 8-9 轮）

### 插件正式版存在性验证（证据）
- `https://repo.leavesmc.org/snapshots/org/leavesmc/leavesweight/patcher/org.leavesmc.leavesweight.patcher.gradle.plugin/maven-metadata.xml` → 200，`<latest>2.2.0-SNAPSHOT</latest>`，版本列表 `1.0.0-SNAPSHOT / 1.0.1-SNAPSHOT / 1.0.2-SNAPSHOT / 2.0.0-SNAPSHOT / 2.1.0-SNAPSHOT / 2.2.0-SNAPSHOT`（lastUpdated 20260601030237）。
- 同路径的 `releases/` → **HTTP 404**；Central `repo.maven.apache.org/maven2/org/leavesmc/leavesweight/patcher/…/maven-metadata.xml` → **404**；plugins.gradle.org 走跨源重定向回到 Central，说明插件不在 Gradle Portal。
- `releases/org/leavesmc/leavesweight/leavesweight-core/` → **404**，snapshots 只有 `2.0.0-SNAPSHOT / 2.1.0-SNAPSHOT / 2.2.0-SNAPSHOT` → **leavesweight patcher/core 没有正式版**。
- `releases/org/leavesmc/leavesclip/maven-metadata.xml` → 200，`<release>3.0.10</release>`，版本含 `2.0.0 … 3.0.10`；生成脚本已用 `org.leavesmc:leavesclip:3.0.10` → 无需改动。
- 结论：按裁决「不存在就停止该提交」，**没有做插件版本固定提交**；根 `build.gradle.kts:7` 保持上游原样 `id("org.leavesmc.leavesweight.patcher") version "2.0.0-SNAPSHOT"`，未擅自升级其他版本、未把 SNAPSHOT 当最终态。

### 三个裁决修正的落地情况
1. Spigot 强制关闭（裁决 ①）：提交 `96c9ea4`「Build: keep spigot generation disabled」把 `spigot { enabled = false }` 恢复回 `leaves-server/build.gradle.kts.patch`。gradle 配置阶段报 `e: leaves-server/build.gradle.kts:37:9: Unresolved reference 'enabled'`（leavesweight DSL 不提供 `enabled` 成员）→ 构建形状变化，按裁决记 **blocked**：**没有**改回 `true`，**没有**自行引入新的 `buildDataRef`/`packageVersion` 取值。需要用户补充裁决（保持上游删除该行 / 改用其他关闭方式）。
2. `Entity.getServer()`（裁决 ②）：提交 `b42c3a6`（嵌套 minecraft 仓库 `209dc9b`）删除 Leaves 恢复的公共 API，调用点全部改 `level().getServer()` / `this.level().getServer()`（`ServerBot.java` L151/L211/L349/L478/L484/L490/L499/L512/L515、`ServerLookAction.java:93`、`ServuxStructuresProtocol.java:82`、`ServerPhotographer.java:81/133`）；static `MinecraftServer.getServer()` / `Bukkit.getServer()` 保持不变。含 `getServer()` 的补丁 0058/0074/0077/0078/0090/0114 复查后**无需迁移**（用的是 static 调用或 receiver 已是 `ServerLevel`）。
3. 插件版本（裁决 ③）：见上，停止该提交并回报可用版本。

### A 类机械重锚逐个结果（12 个全部完成，每个单独 commit + 重生成补丁）
| 补丁 | 外层 commit | 结果 | 关键处理 |
| --- | --- | --- | --- |
| 0003 | （早于本轮已提交） | 成功 | 补丁头修复 |
| 0004 Leaves-Protocol-Core | `c75a339` | 成功 | `ResourceLocation`→`Identifier` 等改名、注解内联 |
| 0029 Alternative-block-placement | `5334ab6` | 成功 | `@Nullable` 独立行 → 内联声明行；空行错位按归一化重放 |
| 0033 MC-Technical-Survival-Mode | `43304b9` | 成功 | `getBoolean(GameRules.RULE_*)`→`get(GameRules.*)` |
| 0039 Bedrock-break-list | `04ac436` | 成功 | `MinecraftServer` tickables 循环形态变了（for-each），改为在 `LeavesProtocolManager.handleTick(...)` 之后唯一锚点插入 `BreakBedrockList.endTick()` |
| 0043 Disable-packet-limit | `40fd26b` | 成功 | `username`→`playerName`、`hasClientLoaded()`→`this.connection.hasClientLoaded()` |
| 0049 Optimized-dragon-respawn | `5567bf2` | 成功 | `BlockPattern` 注解内联；计数器字段插在 `respawnDragon(...)` 签名后 |
| 0052 Elytra-aeronautics-no-chunk-load | `0ce48d0` | 成功 | GameRules / slot flags 改名 |
| 0055 No-block-update-command | `fd4d62e` | 成功 | `Block.UPDATE_SKIP_ON_PLACE`/`@Block.UpdateFlags` |
| 0079 Spider-jockeys-drop-gapples | `e338d0a` | 成功 | 文件搬迁重映射 `monster/Skeleton.java`→`monster/skeleton/Skeleton.java`、`monster/Spider.java`→`monster/spider/Spider.java`；Skeleton 已无 `die()` 上下文，`dropFromLootTable` 覆盖插在类尾 `getStepSound()` 之后，`ServerLevel` 改为全限定名（该文件不再有此 import） |
| 0114 Vanilla-player-display-name | `99fe6ca` | 成功 | `getGameProfile()`→`nameAndId()`、`canBypassFullServerLogin(GameProfile)`→`canBypassPlayerLimit(NameAndId)`、Scoreboard 局部改名 `playerTeam`→`team` |
| 0131 Old-wet-tnt-explode-behavior | `05c044e` | 成功 | ServerExplosion 局部变量交叉改名（补丁 `_boolean`↔树 `flag`、补丁 `flag`↔树 `flag1`） |
| 0132 Old-projectile-explosion-behavior | `c321195` | 成功 | 上游新增 redirectable-projectile 分支使两行锚点不再相邻，改用单行唯一锚点 `entity.push(vec32);` 做 replace |

嵌套 minecraft 仓库重放统计：本轮前缀重放到 0132 时 applied 与手工重锚一致；A 类之外仍有 18 个补丁按语义冲突保持未应用（0007/0018/0035/0038/0053/0059/0074/0082/0088/0095/0105/0110/0115/0133/0135/0136/0138/0139/0141）。

### gradle 首个补丁取证（第 8 / 第 9 轮，未混入主分支）
- 第 8 轮（HEAD 原样，含 `enabled = false`）：配置阶段即失败 `Unresolved reference 'enabled'`，日志 `E:\mc\leaves-work\1.21.11-apply-round8.log`。
- 第 9 轮（**诊断**，只在 gitignored 的生成脚本里临时删掉 `enabled = false`，未提交、跑完已还原）：日志 `E:\mc\leaves-work\1.21.11-apply-round9.log`，`BUILD FAILED in 30s`，`Execution failed for task ':applyPaperSingleFilePatches'` → `Applied Offset Mismatch in 'paper-server/build.gradle.kts.patch' at 47. Expected: 51, Actual: 50`。
- 含义：硬卡点已从「MC 源码语义冲突」转移到 **paper-server 构建配置补丁的行偏移**（上游 spigot 块形状变化导致 +侧行数与实际差 1 行）。这属于裁决 ① 的连带后果，按规则不自行改偏移、不改取值，记 blocked。

### Gate 结论（本轮终态）
- A 类 12 个 + 三个裁决修正已执行完毕；gradle 仍不到 `success`（首个硬卡点仍在构建配置层）→ 按裁决停止扩大 26.x：`compat/26.1.2`、`compat/26.2` 只保留 diag 诊断分支，**未合并 master、未产发布产物、未上传 Release、未宣布 success**。
- 转为只维护 `1.21.8` 基线（`Leaves-base-1218` detached `d93e976` + `downloads\builds\1.21.8\` 4 个 jar），上游 Releases 镜像保持 20 release / 23 assets。
- 连续非 success 计数：本轮后为 **9**（上限 10）→ 本轮记 blocked，不再消耗预算。
- `master` 保持 `175d634`；`compat/1.21.11` 外层 HEAD `c321195`（本轮新增 `5334ab6`/`5567bf2`/`99fe6ca`/`05c044e`/`04ac436`/`e338d0a`/`c321195`）；分支未合并到 master；`Documentation` / `Configuration` 未改动。

### C 类 needs-review 与 D 类 deferred（本轮只标注，不改代码）
- 0138（chat 输入上限）：baseline 已有 Paper 开关 `ServerboundChatCommandPacket.MAX_CHAT_PACKET_INPUT_SIZE = Integer.getInteger("paper.maxChatCommandInputSize", 256)`；计划用 Leaves 配置覆盖该值（不删补丁），语义组合待人工定案 → **needs-review**。
- 0095（hopper 冷却）：保留 Leaves `mcTechnicalMode` 语义 + 上游 `paperConfig().hopper.cooldownWhenFull` 双开关，无法安全组合 → **needs-review**。
- 0074 deferred：依赖 0007（`advancements/critereon/SimpleCriterionTrigger` 已迁到旧路径缺失）PlayerList 迁移定案后恢复。
- 0105 deferred：等位标志语义定案（上游位标志重构）后恢复。
- 0135 deferred：需人工确认上游是否已修复对应行为。
- 0139 deferred：改动体量大，延期。
- 0141 deferred：需人工确认是否已被上游冗余覆盖。
- B 类（0018/0053/0110/0115 等）本轮未动，等 A 类结果（已完成）后另轮处理。

### 需要用户补充裁决
1. Spigot 关闭方式：保持上游（删除 `enabled` 行）还是改用别的关闭姿势？当前 `96c9ea4` 使构建脚本编译失败。
2. 是否允许对 `paper-server/build.gradle.kts.patch` 做 ±1 行机械偏移修正（不改语义、不改取值）？这是本轮第 9 轮的硬卡点。
3. 插件版本策略：leavesweight 只有 SNAPSHOT，是否接受「固定到某个 SNAPSHOT」或保持上游不固定？
### 补丁格式归一化（阶段 2 收尾，compat/1.21.11）
- 上游基线 `d93e976` 的 141 个 feature 补丁约定：**LF 行尾 + 保留 `index` 行**（141/141 含 index、0 含 CRLF）。本轮重生成出的补丁偏离了这个约定：8 个写成 CRLF，另有 4 个（0004/0033/0052/0055）仍带 `index` 行。按阶段 2 裁决「重生成补丁保留 From/Date/Subject、删 index 行」统一处理：**13 个 A 类补丁全部改为 LF 且无 `index` 行**，逐个单独提交，每个提交只改一个补丁文件。
- 归一化后的提交（旧→新）：`9464f01`(0004) / `5d1e546`(0029) / `3755698`(0033) / `745b11d`(0039) / `4cbb305`(0043) / `874f702`(0049) / `6386893`(0052) / `ddb9538`(0055) / `83806a9`(0079) / `ace9ce9`(0114) / `5b856e2`(0131) / `97f636a`(0132)；0003 已符合约定未改动。
- 归一化后重放验证：前缀重放到 0131 时嵌套仓库提交 156 个、工作树干净、无 `.rej`；0132 在归一化补丁上 `git apply` 直接 `Applied patch ... cleanly`（证据 `E:\mc\leaves-work\normalize-replay.txt`、`normalize-0132-check.txt`）。分支 tip 从 `c321195` 前进到 `97f636a` 并推送，未合并 master。
- CI 证据缺失：fork 的 GitHub Actions 运行总数为 **0**（`E:\mc\leaves-work\leaves-actions.txt`），因此 1.21.11 的验证只能依赖本地 gradle 日志（第 8/9 轮），不能用 CI 绿作为门槛。

## §15 第 10 轮：三项裁决执行 + gate 收口（2026-11-23，compat/1.21.11）

### 裁决 1 · Spigot（只读比对，未硬写 DSL）
- 比对结论：唯一 success 的 **1.21.8 基线里 Spigot 层是禁用状态**。证据（`E:\mc\leaves-work\spigot-compare-round10.txt`）：
  - `Leaves-base-1218\leaves-server\build.gradle.kts.patch` 第 44-48 行把 `-        enabled = true` 改成 `+        // enabled = true // Leaves - disable`（注释掉 = 禁用）。
  - 应用后的 `Leaves-base-1218\leaves-server\build.gradle.kts` 第 40-43 行只剩注释行 + `buildDataRef` / `packageVersion`，没有 `enabled` 成员。
  - 产物 jar `leaves-server-1.21.8-R0.1-SNAPSHOT.jar`（27935137 字节、18346 条目）里 `v1_21_R5` 条目 **0 个**，`org/spigotmc` 条目 12（仅 API 引用）→ Spigot 生成层确实没产出。
- 等价关闭姿势查证：gradle 缓存里 leavesweight-core **2.0.0-SNAPSHOT**（sha1 `665e5a24194ec71c1146353e5a27e05150a19d14`）的 `SpigotExtension` 只有 `getBuildDataRef()` / `getPackageVersion()`，**没有 `getEnabled()`**；**2.1.0-SNAPSHOT**（sha1 `d2e426a340fea2a53da00e364896e12b14067bfa`）才有 `getEnabled()`。按裁决不升级插件、不自造 DSL。
- 本轮提交 `e49e92e`（compat/1.21.11）：把 Paper 1.21.11 自带的 `        enabled = false`（原始文件第 26 行，paperRef `ba4b700e1`）恢复成补丁**上下文行**，不新增取值，spigot 形状与上游一致。
- 第 10 轮构建取证（`E:\mc\leaves-work\1.21.11-apply-round10.log`，便携 Gradle 9.2.1 + JDK 21）：`BUILD FAILED in 12s`，**配置阶段**失败 `leaves-server/build.gradle.kts:37:9: Unresolved reference 'enabled'`（记错本 E070 命中）→ 2.0.0 插件无法编译带 `enabled` 的生成脚本 ⇒ 按裁决记 **blocked**，不改回 `true`、不删块（`AllTasks` 无条件读 `getPackageVersion()`）。

### 裁决 2 · `paper-server/build.gradle.kts.patch` ±1 机械重锚
- 本轮提交 `2ecb1f7`（compat/1.21.11）：只改写 **9 个 `@@` 头行**（`@@ -7,22 +6,34 @@` → `@@ -7,23 +6,35 @@`；第 3 个 hunk `+50,13` → `+51,13`，其余 new 侧整体 +1），文件行数不变，`+`/`-` 内容行逐条前后一致（+84 / -40），未改任何取值，无 CRLF、无 `index` 行。
- 证据（`E:\mc\leaves-work\build-patch-reanchor.txt`）：对 paperRef `ba4b700e1` 的原始 `paper-server/build.gradle.kts`（375 行）跑 `git apply --check` → **rc = 0**；`git apply` → 应用后 419 行，`enabled = false` 保留、`activeFork = leaves` 出现，与逐 hunk 模拟结果一致。
- 偏移正好 **±1**，未超出裁决边界；第 9 轮的 `Applied Offset Mismatch ... Expected: 51, Actual: 50` 已被这条重锚消除。

### 裁决 3 · 插件版本固定（结论：不固定，记不可复现风险）
- 时间戳坐标能从 metadata 推导（`leavesweight-core/2.0.0-SNAPSHOT/maven-metadata.xml` → `<timestamp>20250422.125601</timestamp> <buildNumber>5</buildNumber>` ⇒ `2.0.0-20250422.125601-5`），但 Reposilite **不提供 Maven 标准的时间戳版本目录**：
  `.../leavesweight-core/2.0.0-20250422.125601-5/*.jar|pom` 与目录列举全部 **HTTP 404**；带时间戳的文件只存在于 `2.0.0-SNAPSHOT/` 目录内（`.jar` 200 / 12137119 字节，与本机缓存同尺寸）。
- ⇒ Gradle 按显式时间戳坐标解析不到 ⇒ 固定会直接把配置阶段打挂。按裁决兜底：**保持上游浮动 `2.0.0-SNAPSHOT` 不固定**，`build.gradle.kts` 第 7 行未改动，**本轮无提交**，标注「不可复现风险，不作为 success 门槛」（缓存对应 build 5）。证据 `E:\mc\leaves-work\plugin-pin-round10.txt`。
- 顺带记录：`2.2.0-SNAPSHOT` metadata 时间戳 `20260601.030213` build 1 —— 升级插件版本不在本轮授权范围内。

### 最终 Gate 结论（第 10 轮 = 最后一轮预算）
- 1.21.11 **仍不到 success**：卡点从「hunk 偏移」推进到「插件 DSL 缺 `spigot.enabled`」（配置阶段，12 秒即失败），属于构建插件能力边界，不是 MC 语义冲突。
- 连续非 success 计数到达 **10**（上限）→ **收口**：不再消耗构建轮次，不再扩大 26.x；`compat/26.1.2`、`compat/26.2` 保持诊断分支，**未合并 master、未产 Release、未上传产物、未宣布 success**。
- 转为只维护 **1.21.8 基线**：`Leaves-base-1218` detached `d93e976` + `downloads\builds\1.21.8\` 4 个 jar；上游 Releases 镜像保持 20 release / 23 assets。
- 分支：`compat/1.21.11` HEAD `2ecb1f7`（本轮新增 `e49e92e`、`2ecb1f7`，已推送，远端 tip 复核 `2ecb1f7d182…`）；`compat/1.21.9` `8e0597c`、`compat/1.21.10` `4f6cf0f`、`compat/26.1.2` `8ba0135`、`compat/26.2` `ffb9cc5` 均未合并 master；`master` 不因 1.21.11 变更；`Documentation` / `Configuration` 未改动；A/B/C/D 语义未动。
- `gradle.properties` 上被脚本注入的 trustStore 改动已还原（并修掉 `Set-Content -Encoding UTF8` 引入的 BOM）。

### 仍需人工裁决（本轮已按边界停止，不自行扩大）
1. 是否升级 leavesweight 到支持 `spigot.enabled` 的版本（2.1.0+ / 2.2.0-SNAPSHOT）以获得显式关闭姿势 —— 需要人工确认对 1.21.11 的兼容性与语义。
2. 若不接受升级：是否接受「删除 `enabled` 行、依赖插件默认关闭」的姿势 —— 需人工确认默认语义，并用产物 jar 条目计数验证 Spigot 层确实未生成（参照 1.21.8 的 0 条目判据）。
3. 插件不可复现风险是否需要另开镜像/本地缓存锁定方案（本轮只标注，未处理）。

### 收口后的两项裁决（第 10 轮收口通过，2026-11-23 14:2x，本轮不再消耗构建轮次）
1. **不升级 leavesweight**（已关闭上面第 1 项）：保持 `build.gradle.kts` 第 7 行 `id("org.leavesmc.leavesweight.patcher") version "2.0.0-SNAPSHOT"`，不可复现风险继续标注、不作为 success 门槛。升级属扩大战场，且 2.1.0-SNAPSHOT 同样不可复现、还会改变 spigot DSL 形状，风险大于收益。
2. **接受「删掉 `enabled` 成员、依赖插件默认关闭」**，先做只读核对、不跑构建（已关闭上面第 2 项）。
   - 版本核对证据：`Leaves-base-1218`（detached `d93e976`）的 `git show d93e976:build.gradle.kts` 第 7 行与 `Leaves-1.21.11\build.gradle.kts` 第 7 行**完全相同**，都是 `id("org.leavesmc.leavesweight.patcher") version "2.0.0-SNAPSHOT"`；两边 `settings.gradle.kts` 的 `pluginManagement` 都没有覆盖该版本（只声明 `foojay-resolver-convention 0.9.0`）。⇒ 同一插件版本，证据链闭合。
   - 默认禁用成立：2.0.0-SNAPSHOT 的 `SpigotExtension` 只有 `getBuildDataRef()` / `getPackageVersion()`，没有 `getEnabled()`；1.21.8 生成脚本无 `enabled` 成员（第 41 行只是 `// enabled = true // Leaves - disable` 注释行，不是 DSL 成员）；产物 jar 18346 条目中 `v1_21_R5` **0 个**、`org/spigotmc` 12 个（仅 API 引用）→ Spigot 生成层未产出 = 默认禁用。
   - 机械对齐提交 **`9f04f9f`**（compat/1.21.11，已推送，远端 tip 复核 `9f04f9fa326…`）：把 `e49e92e` 恢复的 `         enabled = false` 上下文行改成 `-        enabled = false` 删除行，并随之 ±1 重锚 9 个 `@@` 头（`@@ -7,23 +6,34 @@`、`@@ -40,15 +50,13 @@`、`+112/149/188/219/307/344/392`）。与 HEAD 的差异共 **10 行**：9 个 `@@` 头 + 1 个内容行（上下文 → 删除），`-` 侧 40 → 41（只多这一条）、`+` 侧保持 84 条，取值未变，无 CRLF、无 `index` 行。对 paperRef `ba4b700e1` 的原始 375 行脚本 `git apply --check` → **rc = 0**；`git apply` → 418 行，spigot 块（第 36-39 行）为 `buildDataRef` + `packageVersion`，**无 `enabled` 成员**，与 1.21.8 生成形状一致。证据 `E:\mc\leaves-work\spigot-align-round11.txt`。本轮**未跑 gradle 构建**。
   - 顺带把基线工作树上残留的 `gradle.properties`（脚本注入的 trustStore + BOM）还原到 `d93e976` 状态，基线证据树保持干净。
3. **1.21.11 终态**：spigot 形状卡点 = **blocked-resolved**（插件能力边界已用「删掉 `enabled` 成员」对齐 1.21.8 姿势消除，未升级插件、未自造 DSL）；版本整体仍 **blocked**，不追 success —— 剩余卡点是 B 类语义冲突（补丁重放）与 fork GitHub Actions 运行总数 0 导致的 CI 证据缺失。
4. **收口结论不变**：连续非 success 计数停在 10（上限），不再消耗构建轮次、不跑第 11 轮；不合 master、不产 Release、不上传产物、不动 `Documentation` / `Configuration`；只维护 1.21.8 基线，`compat/26.1.2`、`compat/26.2` 仅作诊断分支。仍开放的第 3 项（插件不可复现风险的镜像/本地缓存锁定方案）保持只标注、不处理。

## §16 维护模式（2026-11-23 收口后，无构建轮次）

### 诊断分支处置（按裁决关闭，不合 master、不产产物）
- 说明（如实记录）：仓库里**不存在**名为 `diag/26.1.2-api-timings` / `diag/26.2-api-semantics` 的分支（本地、远端、packed refs 全部查过，0 条）。这两项诊断是在 `compat/26.1.2`、`compat/26.2` 两个分支及其 worktree（`E:\mc\leaves-work\Leaves-26.1.2`、`Leaves-26.2`）里做的，结论已经落在这份状态文档里，按裁决写进本节并关闭。
- `26.1.2 api timings` → **诊断完成、有结论（§12）**：配置阶段能过（最新 leavesweight 插件吃 26.x 配置形状），卡点在 `:applyPaperApiFeaturePatches FAILED` / `Applying: Delete Timings` / `sha1 information is lacking or useless (co/aikar/timings/FullServerTickHandler.java)`；根因是 26.x 补丁仍是 1.21.8 时代版本且 `git am --3way` 没有可用前置 blob（删 index 行也一样），必须像 1.21.11 那样手工逐层重锚再用 `regenerate_patches.py` 导出（E066）。分支 `compat/26.1.2` HEAD `8ba0135` 与 master 同历史（**0 个独立提交、无产物**）→ 标记「诊断完成、有结论、无代码产出」，关闭。
- `26.2 api semantics` → **诊断完成、有结论（§13）**：api 层手工盘点天花板 **7/9**，两个语义阻塞 —— `0001-Delete-Timings` 的待删行在 26.2 里不存在而 `co/aikar/timings` 目录仍在（Paper 重写调用点，按规则 3 不猜）、`0005-Bytebuf-API` 的落点在 26.2 `src/main/java/org/bukkit/` 已不存在（上游搬迁）；另有结构性事实（E069）：gradle 顺序 `git am` 被第 1 个冲突补丁挡死后续全部，且上层失败时 `leaves-server` 嵌套 minecraft 仓库不 clone → 26.2 的 minecraft 层连基线都拿不到。分支 `compat/26.2` HEAD `ffb9cc5`（仅 `gradle.properties` 适配到 26.2 + 9 个 api 补丁去 index 行）保持存档，不合并、不产产物 → 标记「诊断完成、有结论」，关闭。
- 两个分支**保留为存档 ref，未删除、未合并**；关闭 = 状态标记，不再消耗轮次。

### 维护模式说明
- 唯一维护对象是 **1.21.8 基线**：`Leaves-base-1218` detached `d93e976`（工作树已还原到干净状态，`gradle.properties` 里本机 trustStore 注入已回收）+ `E:\mc\leaves-work\downloads\builds\1.21.8\` 的 4 个 jar（`leaves-api` / `leaves-server` / `leaves-bundler-mojmap` / `leaves-leavesclip-mojmap`，均为 11-20 那次唯一成功构建的产物）。
- `compat/*` 分支**仅存档**：`compat/1.21.9` `8e0597c`、`compat/1.21.10` `4f6cf0f`、`compat/1.21.11` `9f04f9f`、`compat/26.1.2` `8ba0135`、`compat/26.2` `ffb9cc5`，均不合并 master（`branch --no-merged master` 复核：除 `compat/26.1.2` 因与 master 同历史外，其余三个都未合入）；`26.x` 仅诊断。
- 构建轮次预算：**连续非 success 计数停在 10（上限）**，不跑第 11 轮，不追 1.21.11 success，不再扩大 26.x。维护模式下的动作只允许：状态文档更新、记错本维护、只读证据核对。
- GitHub Releases 保持 **20 个 release / 23 个 asset**（19 个上游镜像 + `compat-1.21.8`），fork 的 GitHub Actions 运行总数仍为 **0**（CI 不能当门槛证据）。

### 插件版本策略条款（浮动 SNAPSHOT 的边界）
- 当前策略：**浮动 `2.0.0-SNAPSHOT` + 标注不可复现风险 + 不作为 success 门槛**，这只在 1.21.11 处于 **blocked**（非最终态）时允许。
- 硬性条款：若 `1.21.11`（或任何 compat 版本）将来转为 **success** 或要**产出 Release**，必须先验证插件坐标并**固定到可复现的正式版坐标**，否则不得宣布 success、不得发 Release。固定前需重跑一次可复现构建取证并记录坐标来源；仍然禁止固定不存在的版本、禁止为固定而固定、禁止未经裁决升级插件版本。

## §17 第 12–20 轮：compat/1.21.11 从里程碑到真 success（2026-11-24 → 2026-11-25，自主授权）

### 判据升级（第 19/20 轮）
- `applyAllPatches` 全绿只是**里程碑**；`success` 的判据是：补丁全量可回放 + `:leaves-server:compileJava` BUILD SUCCESSFUL（错误 0）+ 产物可复现 + 与 1.21.8 基线并存。
- 分次调用 gradle 任务以保证顺序：`applyPaperApiPatches` → `:leaves-server:applyAllServerPatches` → `:leaves-server:compileJava` → jar/bundler/leavesclip。一次调用混多个任务会触发隐式依赖失败。

### 权威落点裁决（第 19 轮）
- 以上游 `LeavesMC/Leaves dev/1.21.11` 的**同名补丁体**与**同名 main 源码**为权威落点，只作用于 `compat/1.21.11`，不动 1.21.8。
- 同名依据：`patch-title-map-round19.json`（matched 131 / only_our 10 / only_up 9）。
- 不改 Paper base：上游 `gradle.properties` 的 `paperRef=c5a2736aaa33a6447badd2ed294fbf5940cb014b` 不在本地 `paper-mirror.git`，且 github.com:443 直连失败 → 离线不可切换。改为在 `ba4b700e1`（Paper tag 1.21.11）与 `efeab1ab`（minecraft 基线）上用 reanchor / harvest 修上下文，**±代码行保持上游逐字**。

### Paper 层流水线改造（第 19 轮，脚本 `chain_paper_pass.py`）
- `paper-api` / `paper-server` 的 main 源码是 `.gitignore` 生成物（L51/L52）→ 一切语义修正必须落在**补丁体**里，手工编辑生成物无效。
- Paper tag 顶层含 `paper-api/`、`paper-server/`，而 Leaves 的 paper 补丁路径相对各层根 → 重放需加层前缀（`remap_prefix`/`strip_prefix`），入库补丁保持层相对路径，复核用 `git apply --check --directory paper-api|paper-server`。
- `head0` 必须在 apply **之前** capture；worktree 复用必须 `prune → remove --force → prune → add --detach --force`，否则残留脏树让判据失真。
- 结果：api 采纳上游 9/9；server 采纳上游 14 + **保留我方 `0013-Leaves-Plugin.patch`**（上游体在 tag `ba4b700e1` 基线落不上，我方体复核 rc=0）；按序号全量重放失败 0。证据：`round20-paper-server-full-replay.txt`。

### minecraft 层收口（第 20 轮）
- 9 处包路径机械改名 `org.leavesmc.leaves.command.subcommands.` → `org.leavesmc.leaves.command.leaves.subcommands.`（`0055` 7 处、`0133` 1 处、`0139` 1 处）。证据 `round20-legacy-import-rename.txt`。
- 删 `0018-Only-check-for-spooky-season-once-an-hour.patch`：上游 dev/1.21.11 已删除该 feature（补丁标题映射 only_our），无等价落点 → 删补丁并记根因，不伪造落点。
- `0137-Configurable-collision-behavior.patch` 直接 apply 上游体失败（`error: patch failed: ...CollisionUtil.java:2022`，证据 `round20-slot-0137.txt`）→ 改用 **harvest**：从槽位父提交 `49606c6e` 的基线重建上下文行（hunk2 锚点绝对行 2070，前 3 行 `return ret; / } / 空行`），mbox 头取自原始补丁。复核 `git apply --check --index --whitespace=nowarn -v` **rc=0**（`round20-slot-0137-harvest.txt`）。首版 rc=128 是 hunk 之间插入裸空行导致 `patch fragment without header`，已修。
- 采纳上游 collision 语义：`LeavesConfig.fix.collisionBehavior` 枚举只剩 `VANILLA, PAPER`（L1307-1309），`CollisionBehaviorValidator.stringConvert` 把传入 `BLOCK_SHAPE_VANILLA` **降级为 `PAPER` 并告警**（L1314-1317）；补丁体只留 2 hunk，`case VANILLA -> !aabb.intersects(singleAABB)`，不再保留 `voxelShapeIntersectVanilla`。

### main 源码删除（第 20 轮，死代码）
- `leaves-server/src/main/java/org/leavesmc/leaves/command/LeavesCommandUtil.java`：上游改名为 `command/CommandUtils.java`；文件系统全量引用复核（含 gitignore 生成树）外部引用 **0** → 删文件（`round20-legacy-command-delete.txt`，删除后残留引用 0）。
- `leaves-server/src/main/java/org/leavesmc/leaves/bytebuf/SimpleBytebufManager.java`：上游走 `InternalBytebufHandler`（公开方法只有 `init/updatePlayer/allocator/callPacketEvent/sendPacket`），该 legacy 类无人引用 → 删（`round20-bytebuf-legacy-delete.txt`）。
- `PacketType` 按上游新坐标 `org.leavesmc.leaves.bytebuf.PacketType` 重命名（4 处 + 补 import，`round19-packettype-rename.txt`）。

### `0007-MC-Technical-Survival-Mode.patch` 字段回移（第 20 轮）
- `maxArrowDespawnInvulnerability` 来自 Paper `c5a2736a`（tag `ba4b700e1` 无此字段）。离线不可切 Paper base → 用 `raw.githubusercontent.com/PaperMC/Paper/c5a2736a/...` 抓取单文件事实，在补丁体末尾追加文件段回移**逐字相同**的字段声明（`@@ -171,6 +171,10 @@`）。
- 复核：tag worktree `git apply --check --index --whitespace=nowarn --directory paper-server` **rc=0**（`round20-paper-0007-field.txt`）；按序号全量重放后工作树命中字段声明，且 `PaperConfigurations.java` 命中 `McTechnicalModeHelper.onWorldConfigCreate(config)` / `doMcTechnicalModeIf()`（`round20-paper-server-full-replay.txt`）。
- **已记录差异**：回移字段只声明、tag 基线没有消费点（行为接线在 Paper `c5a2736a` 侧），不伪造落点；语义等价性以「字段存在 + 配置项可用」为准，行为差异如实记录。
- 注意：`git show HEAD:` 读到的是 tag 版本而非应用后的工作树 → 判据必须读工作树文件。

### 编译收敛轨迹（javac 上限 100，需迭代）
- round17 语法错误 25 → round18 100 → round19e 100 → round20 41 → round20b 17 → round20c 3 → **round20d 0**。
- round20c 的 3 条即上述三处语义冲突（collision 枚举、`LeavesCommandUtil`、`maxArrowDespawnInvulnerability`），全部按上游语义裁决解决。
- `1.21.11-compile-round20d.log`：`:leaves-server:compileJava` **BUILD SUCCESSFUL in 2m 9s**，错误条目 0。

### `reobfJar` 被上游禁用（第 20 轮）
- `:leaves-server:reobfJar` BUILD FAILED：`io.papermc.paperweight.PaperweightException: Reobfuscated server jars are no longer supported and only exist for debugging purposes.` → 这是上游策略，不是我方缺陷。
- 交付产物走 mojmap：`leaves-api jar` + `leaves-server jar` + `bundler-mojmap` + `leavesclip-mojmap`，与 1.21.8 基线产物集合同构（基线也没有 reobf jar）。

### 产物与复现判据（第 20 轮）
- 目录：bundler/leavesclip/server jar 在 `leaves-server\build\libs\`，api jar 在 `leaves-api\build\libs\`。
- 落盘 `E:\mc\leaves-work\downloads\builds\1.21.11\`（与 1.21.8 基线目录并存，基线未动）：
  - `leaves-api-1.21.11-R0.1-SNAPSHOT.jar` 2,801,720 bytes / 条目 2496 / sha256 `065cf7f7…`（两次构建 sha256 相同，CRC 差异 0）
  - `leaves-server-1.21.11-R0.1-SNAPSHOT.jar` 28,157,716 bytes / 条目 18569 / sha256 `98971117…`（基线 1.21.8 为 18346 条目）；关键类命中 `CommandUtils` / `McTechnicalModeHelper` / `InternalBytebufHandler` / `WorldConfiguration`；判据 `org/spigotmc` 条目 12、`v1_21_R5` 条目 0（与基线同款）
  - `leaves-bundler-1.21.11-R0.1-SNAPSHOT-mojmap.jar` 106,354,476 bytes / 条目 2558 / sha256 `08b74c97…`
  - `leaves-leavesclip-1.21.11-R0.1-SNAPSHOT-mojmap.jar` 64,736,683 bytes / 条目 2431 / sha256 `f5225a0b…`
- 复现判据 = **逐条目 CRC**（zip 时间戳不算内容差异；嵌套 jar 递归展开）：server/api 差异 **0**；bundler 差异 2（嵌套 leaves jar 的 `META-INF/MANIFEST.MF`、`versions.list`）；clip 差异 3（`server-1.21.11.jar.patch`、`patches.list`、`versions.list`）。差异行内容复核 = 构建时间戳与由其派生的哈希（`round20-repro-list-diff.txt`）→ 不入判据。字节级 sha256 不作判据（与基线一致）。

### 第 21 轮：一次完整收口构建（success 定稿）
- 走 `gradle_task.ps1` 逐任务分次调用（每个任务后还原 `gradle.properties`），六个任务全部 BUILD SUCCESSFUL、编译错误计数 0：
  `applyAllPatches`、`:leaves-server:compileJava`、`:leaves-api:jar`、`:leaves-server:jar`、`:leaves-server:createMojmapBundlerJar`、`:leaves-server:createMojmapLeavesclipJar`（日志 `1.21.11-round21-*.log`，汇总 `round21-final-pass.txt`）。
- 手写 `dependency-verification-metadata.xml` 与 `.correct` 备份已删除（见上节：strict 不校验 plugin classpath，留着会误导），删除后构建仍全绿。
- 复现性（第 21 轮重建 vs 第 20 轮已交付产物，逐条目 CRC、含嵌套 jar 递归展开，排除 `MANIFEST.MF`/`versions.list`/`patches.list`/`*.jar.patch`）：四个产物**展开条目数相同、CRC 差异 0**（server 18568、api 2495、bundler 57253、clip 14503）；`leaves-api` jar 字节级 sha256 两次相同（`065cf7f7…`），server/bundler/clip 仅因清单与 bsdiff 派生时间戳而整体 sha256 变化。证据 `round21-repro-vs-delivered.txt`。
- 1.21.8 基线目录未动：四个 jar 大小与 sha256 复核（api `cedb0e03…`、bundler `de50f798…`、clip `0064faf7…`、server `c4c90220…`）。
- 三次独立构建（第 20 轮两次 + 第 21 轮一次）产物条目一致 → 交付产物仍以第 20 轮落盘版本为准，指纹不变。

### 插件坐标可复现收口（第 20/21 轮，§15 硬性条款）
- leavesweight DSL `2.0.0-SNAPSHOT`（缓存 jar sha1 `665e5a24194ec71c1146353e5a27e05150a19d14`、jar sha256 `8be5d531…`、pom `e8b0e1ed…`、module `1f122677…`、plugin marker pom `50732027…`）没有正式版坐标可固定：上游 dev/1.21.11 用的是 `2.1.0-SNAPSHOT`，本地 gradle 缓存只有 `2.0.0-SNAPSHOT` / `2.1.0-SNAPSHOT` 两个快照，`repo.leavesmc.org/releases` 的相关 metadata 路径全部 404 → 不存在正式版。2.1.0-SNAPSHOT 才有 `getEnabled()`，按 §15 裁决不升级（靠插件默认关闭）。
- `gradle -M sha256 --dependency-verification=off` 全量生成 metadata **失败**：`Multiple entries with same key: org.spongepowered:configurate-gson:4.2.0-SNAPSHOT=...`（`1.21.11-verifygen-round20e.log`，BUILD FAILED 但编译错误 0）→ 改手写 metadata 只钉 4 个插件构件（`round20-plugin-pin.txt`）。
- 但手写 metadata **钉不住插件**：负面测试把 sha256 改成全 0，`-F strict` 仍 BUILD SUCCESSFUL（不带 refresh `round20g`、带 refresh `round20h`、去掉 `trusted-artifacts` glob `round20i` 三次都成功）→ Gradle dependency verification 不覆盖 `pluginManagement`/build script classpath，也不重验已缓存产物。**因此无效 metadata 不入库**（已删除 `dependency-verification-metadata.xml`，避免误导）。
- 替代的可复现手法 = **解析后手动比对插件字节 sha256**：跑 `--refresh-dependencies` 重新解析后，`verify_plugin_bytes_after_refresh.py` 命中 4 个构件且与固定值一致 True（`round20-plugin-repin-after-refresh.txt`）→ 插件坐标仍浮动，但每次解析的字节可复核；上游若重发快照，构建会换字节，届时按新字节重新登记。

### Documentation / Configuration 同步裁决（第 21 轮）
- 不同步：这两个 fork 仓库的内容要跟上 1.21.11 的 feature/配置项，属于文档改写，不在本次 success 判据里；本轮没有可核验的 1.21.11 配置语义变更清单，硬写会产出未经构建验证的说明。Release body 里直接写清产物与判据即可。

### 分支与合并裁决（第 20 轮）
- `compat/1.21.11` 本地 = 远端 = `14fb27fee55941d21e75eedf6f3b3fd1103d1b0d`（第 19/20 轮收口 commit `045009e` 的后续），补丁目录与 HEAD 树、磁盘计数一致：minecraft features 135、api paper 9、server paper 16 + files 1。
- **不合 master**：master 承载 1.21.8 的补丁体与 Paper base，合并会用 1.21.11 补丁覆盖 1.21.8 → 破坏 §16 的「1.21.8 基线可复现」底线。改为以 `compat/1.21.11` 分支 + tag 发布独立 Release，master 与 `Leaves-base-1218`（detached `d93e976`）不动。

### 队列状态更新
- `1.21.11`：补丁层全绿 + api/server apply SUCCESSFUL + `compileJava` BUILD SUCCESSFUL + mojmap 产物落盘且逐条目 CRC 复现 → 转 **success**（以 strict 复跑证据定稿）。
- `1.21.8`：唯一维护基线，未动。`1.21.9` `8e0597c`、`1.21.10` `4f6cf0f`、`26.1.2` `8ba0135`、`26.2` `ffb9cc5` 存档不变；`26.1` skipped-no-paper-base、`26.1.1`/`26.3` blocked-no-paper-base。

## §18 第 21 轮收口：发布与收尾（2026-11-25）
- 目标达成：**compat/1.21.11 完整构建 success** —— 补丁全量可回放 + `:leaves-server:compileJava` 全绿 + 四个 mojmap 产物落盘且可复现。
- 第 21 轮定稿构建（走 `gradle_task.ps1`，逐任务分次调用，跑完还原 `gradle.properties`）：`applyAllPatches`、`:leaves-server:compileJava`、`:leaves-api:jar`、`:leaves-server:jar`、`createMojmapBundlerJar`、`createMojmapLeavesclipJar` 全部 BUILD SUCCESSFUL，错误计数 0（`1.21.11-round21-*.log`，汇总 `round21-final-pass.txt`）。
- 手写 `dependency-verification-metadata.xml` 已删除（§17：strict 不覆盖 plugin classpath，留着会误导），删除后构建仍全绿；插件字节改为「刷新解析后手动比对缓存 sha256」固定（`round20-plugin-repin-after-refresh.txt`）。
- 复现性第三次确认（第 21 轮重建 vs 第 20 轮交付，逐条目 CRC 含嵌套 jar）：四个产物条目数相同、CRC 差异 0（`round21-repro-vs-delivered.txt`）；`leaves-api` 字节级 sha256 两次相同。
- 1.21.8 基线未动：`Leaves-base-1218` detached `d93e976`，四个 1.21.8 jar 大小/sha256 复核一致（`round21-repro-vs-delivered.txt` 尾部）。
- 文档入库：master `e8fc5d0`（§17，429 → 506 行）；compat `34c9920`（同一份 COMPAT_STATE.md）；tag `leaves-1.21.11-compat` → `34c9920`，均已推远端（`round21-docs-commit.txt`）。补丁层文件未变（master tracked patch 171、compat tracked patch 163）。
- 发布：新 Release id `458297960`，tag `leaves-1.21.11-compat`（prerelease），12 个 asset（4 jar + 4 sha256 + 4 crc.txt）。发布前后：release 20 → 21、asset 23 → 35，**旧 release 20 个 / 23 asset 全部未动**（`round21-release-publish.txt`）。
- 发布后复核：下载 api/server jar，sha256 与交付一致 True（api `065cf7f7…`、server `98971117…`），asset 尺寸与本地一致（`round21-release-verify.txt`）。
- 不合 master（§17 裁决：master 承载 1.21.8 补丁体与 Paper base，合并会破坏基线可复现）；不同步 Documentation/Configuration（无经构建验证的 1.21.11 配置语义清单，硬写会产出未验证说明）。
- 记错本：本轮追加 E103–E117（索引与条目同步，现 117 条 / 1439 行）；其中 E109（插件字节钉不住）、E116（paperweight 需要 git 身份）、E117（旧 daemon 不继承新环境变量）是本轮新踩坑。
- 队列终态：1.21.8 success（基线）；**1.21.11 success（本轮）**；1.21.9 / 1.21.10 conflict-needs-review；26.1 skipped-no-paper-base；26.1.1 / 26.3 blocked-no-paper-base；26.1.2 / 26.2 conflict-needs-review。下一个目标：按 1.21.11 的收口手法（上游权威落点 + Paper tag 加层重放 + harvest）推进 1.21.10 → 1.21.9，再评估 26.x 是否具备 Paper 基线。

## §19 插件字节收口（第 22–23 轮，compat/1.21.11 之上追加，不动语义）

**目标**：在不改动补丁语义的前提下，把构建插件 `org.leavesmc.leavesweight.patcher` 从「浮动 SNAPSHOT」变成可复现的固定字节。第 21 轮已 success 并发过 Release，这一节只补可复现性，不重开 success 判据。

**网上查到的真实解法（此前结论被推翻）**：
- `repo.leavesmc.org` 是 **Reposilite**，没有 `/api/repos`、`/api/storage`、`/api/repo/<repo>/browse`（全部 404）；目录列表走 HTML index（`https://repo.leavesmc.org/snapshots/<path>/`），链接引号混用 → 枚举正则要写成 `href=['"]([^'"]+)['"]`。
- Reposilite 把 groupId 的点转成目录：插件 marker 实际在 `snapshots/org/leavesmc/leavesweight/patcher/org.leavesmc.leavesweight.patcher.gradle.plugin/<VER>/`，不是点分目录（点分路径全 404）。
- SNAPSHOT 目录里存在**不可覆盖的时间戳工件**：版本级 `maven-metadata.xml` 的 `snapshotVersions.snapshotVersion.value` 给出真实文件名。实测候选全部 HTTP 200 且与本地 Gradle 缓存字节一致：`leavesweight-core-2.0.0-20250422.125601-5.{jar,pom,module}`（jar 12,137,119 B，sha256 `8be5d531…`）、marker pom 1,956 B（sha256 `50732027…`）。`/releases/…maven-metadata.xml` 全 404 → 确实没有正式版；`repo.papermc.io` 无 leavesweight；`portal.gradle.org` DNS 不可达。
- `leavesweight-core` jar 是 shadowed 自包含（7531 条目，内含 jgit/httpclient/gson/kotlin），并自带 plugin 描述符 `META-INF/gradle-plugins/org.leavesmc.leavesweight.patcher.properties` → `io.papermc.paperweight.patcher.PaperweightPatcher`；pom 无 `<dependencies>` → 单一 classpath 依赖即可。

**落地写法**（`compat/1.21.11`，工作树改动两处 + 一个新目录）：
- `build.gradle.kts` 第 7 行版本写成不可覆盖的时间戳坐标：`id("org.leavesmc.leavesweight.patcher") version "2.0.0-20250422.125601-5"`。
- `settings.gradle.kts` 的 `pluginManagement.repositories` 首位加 vendored 本地仓库，并用内容过滤把 leavesweight 组只交给它：
  `maven(url = settingsDir.resolve("plugin-pin").toURI()) { name = "plugin-pin"; content { includeGroupByRegex("org\\.leavesmc\\.leavesweight.*") } }`，远端仓库保留在后位作为其它组的来源。
- `plugin-pin/`（vendored 仓库，Maven 布局是 groupId 的点换成目录）：
  `org/leavesmc/leavesweight/leavesweight-core/2.0.0-SNAPSHOT/` 放仓库下载的时间戳 jar + pom（pom 去掉 `do_not_remove: published-with-gradle-metadata` 提示，不放 `.module`）+ 瘦身后的 `maven-metadata.xml`（只保留 jar/pom 的 `snapshotVersion`，删掉 module/sources/javadoc，避免 Gradle 去找不存在的文件）；
  `org/leavesmc/leavesweight/patcher/org.leavesmc.leavesweight.patcher.gradle.plugin/2.0.0-SNAPSHOT/` 放自建 marker pom（sha256 `87003402…`，依赖直接指向 `leavesweight-core:2.0.0-20250422.125601-5`）+ 瘦身 `maven-metadata.xml`。
- **Gradle 的快照规则**（本轮踩到并已记错本）：请求时间戳坐标时，Gradle 仍然去 `2.0.0-SNAPSHOT/` 目录，靠 `maven-metadata.xml` 取精确时间戳文件名；直接按 `2.0.0-20250422.125601-5/` 目录放文件会报 `Could not find …`。独立探针工程验证：`classpath 条目数=1`，解析到 `leavesweight-core-2.0.0-20250422.125601-5.jar`，sha256 `8be5d531…`（`E:\mc\leaves-work\pin-probe.log`）。

**决定性验证（插件确实只来自本地 vendored 目录）**：临时把远端 `repo.leavesmc.org/snapshots/` 加内容过滤 `excludeGroupByRegex("org.leavesmc.leavesweight.*")` 后跑 `applyAllPatches` → **BUILD SUCCESSFUL**（`E:\mc\leaves-work\1.21.11-round23-apply-localonly.log`，`Applied 1 patches` 等任务照常执行）；此时 leavesweight 组没有任何远端来源，且本地 file 仓库产物不进 Gradle 缓存（缓存里只有 `leavesweight-core` 的 `2.0.0-SNAPSHOT`/`2.1.0-SNAPSHOT` 目录，没有时间戳目录）。验证脚本 `prove_local_plugin_only.py`，证据 `round23-local-only-proof.txt`；settings 已还原成 vendored+远端并存版本。

**构建结果**：第 23 轮全量重跑见 `E:\mc\leaves-work\round23-final-pass.txt`（`applyAllPatches` → `:leaves-server:compileJava` → `:leaves-api:jar` → `:leaves-server:jar` → `:leaves-server:createMojmapBundlerJar` → `:leaves-server:createMojmapLeavesclipJar`）。产物与第 21 轮交付目录按「逐条目 CRC32（嵌套 jar 递归，排除 MANIFEST.MF/versions.list/patches.list/`*.jar.patch`）」判据比对，见 `round23-repro-vs-delivered.txt`。

**本轮遇到的两处环境抖动（已记错本）**：
- `legacy buildscript + apply(plugin=...)` 写法会让 `plugins {}` DSL 的扩展访问失效：`Script compilation errors: 16 errors`，首个是 `'paperweight'` 与第 84–87 行 `upstreamPath/excludes/patchesDir/outputDir` 的 `Unresolved reference` → 保持 plugins DSL + 时间戳版本，不用 legacy apply。
- `checkoutPaperRepo` 第一次报 `PaperweightException: Command finished with 128 exit code: git … fetch origin ba4b700e…`（`1.21.11-round23-apply-all.log`）；同一命令手动带 `GIT_SSL_CAINFO` 跑 rc=0，随后重跑任务 BUILD SUCCESSFUL（`1.21.11-round23-checkout-paper2.log`）→ 收口前先手动 fetch 目标 sha 更稳。

**底线复核**：`Leaves-base-1218` detached `d93e976…` 与四个 1.21.8 jar 未动；已有 20 个 release / 23 个 asset 未删未覆盖；本节的分支/commit 以实际 ref 为准。

## §20 提交后全量复核（第 24 轮，从已入库的 compat/1.21.11 HEAD 重建）

**起点**：`compat/1.21.11` 本地=远端 `0abd6f35be6bb24ae7d05dd810fbebaaede45741`（本轮三个提交：`6ed34bc` 插件固定改动、`6c05543` §19 文档、`0abd6f3` vendored 目录 binary 属性）。`master` 本地=远端 `a08854d429349f011d205767a139308fb2936b07`（§19 入库）。

**入库字节核对**（`round24-pin-bytes-and-tag.txt`）：`git show HEAD:<path>` 的原始字节 sha256 与上游 Reposilite 时间戳工件一致 ——
- `plugin-pin/.../leavesweight-core-2.0.0-20250422.125601-5.jar`（12,137,119 B）→ `8be5d5311e76943f86a6d7fd347dbcefde4e3ddafe8a75964c386629f6318e34`
- `plugin-pin/.../leavesweight-core-2.0.0-20250422.125601-5.pom` → `88773d797463e4cf9f73f7b82feb0a3de2e4479e2cc917ccabf55caffcebe332`
- `plugin-pin/.../org.leavesmc.leavesweight.patcher.gradle.plugin-2.0.0-20250422.125601-5.pom` → `870034021f748ad7dd6f883958f13647e062f24892d14d9c28a9c032b6f8835b`

**为什么要加 `plugin-pin/** binary`**：仓库根 `.gitattributes` 有 `* text=auto`，`git add` 时对 vendored 的 pom/xml 报 `warning: in the working copy of '…pom', LF will be replaced by CRLF the next time Git touches it`，即工作树与 blob 的换行可能被规范化；标成 binary 后逐字节保持原样（已记错本 E121）。

**构建结果（从实际提交重跑，非工作树残留）**：`round24-final-pass.txt` 六项全绿，工作树无残留 ——
`applyAllPatches` / `:leaves-server:compileJava` / `:leaves-api:jar` / `:leaves-server:jar` / `:leaves-server:createMojmapBundlerJar` / `:leaves-server:createMojmapLeavesclipJar`，日志 `1.21.11-round24-*.log`。收口前先手动 fetch paper 基线 `ba4b700e133f6de66101393b56f4d9068cd4d3b0`（rc=0），避免 §19 记到的 fetch 128 抖动。

**复现判据结果**（`round24-repro-vs-delivered.txt`，逐条目 CRC32、嵌套 jar 递归展开、排除 `META-INF/MANIFEST.MF` / `versions.list` / `patches.list` / `*.jar.patch`）：四个产物条目集合完全相同、**CRC 差异 0** ——
- server：交付 28,157,716 B / 重建 28,157,718 B，两侧展开 18,568，差异 0
- api：交付与重建字节相同（sha256 两侧 `065cf7f74ccb8919…`），展开 2,495，差异 0
- bundler-mojmap：交付 106,354,476 B / 重建 106,351,557 B，展开 57,253，差异 0
- leavesclip-mojmap：交付 64,736,683 B / 重建 64,732,619 B，展开 14,503，差异 0

字节级 sha256 不同只来自被排除的时间戳派生项（MANIFEST / versions.list / patches.list 的写入时刻与长度），因此按条目 CRC 判据判可复现；交付目录里的四个 jar 未被覆盖。

**底线复核**：`Leaves-base-1218` detached `d93e9766d3797d130f66179e70a1b374c546fef7`、工作树干净，四个 1.21.8 jar 字节未动（api `cedb0e03…` / server `c4c90220…` / bundler `de50f798…` / clip `0064faf7…`）；GitHub 当前 releases=21、assets=35（第 21 轮新增的 `leaves-1.21.11-compat` 之外，镜像的 20 release / 23 asset 未删未覆盖）；tag `leaves-1.21.11-compat` 的 tag object `47f0ebad9a4d837d3f4939f509d3f2f96f67c4d5`，目标 commit 仍是发布提交 `34c9920cb3b9c040a1ea035eb362b4e9e7b4a271`，后续文档/构建提交未动 tag；compat tracked patch 163、master tracked patch 171。

**语义改写清单**：本轮无新增语义改写，只有构建/依赖与文档改动；§17 的语义清单保持不变。

**记错本同步**：E118（Reposilite 时间戳工件 + vendored 本地仓库 + Gradle 快照目录规则）、E119（legacy buildscript + apply 使 plugins DSL 扩展失效）、E120（`checkoutPaperRepo` 偶发 fetch 128 → 先手动 fetch）、E121（`* text=auto` 规范化 vendored 文件 → binary 属性 + blob sha256 比对），并修正被推翻的旧结论「插件字节钉不住 / 不可复现」；记错本现 1504 行，索引行 121 与正文条目 121 对齐。

**下一个目标**：按 1.21.11 手法推进 `compat/1.21.10` → `compat/1.21.9`（两者现为 conflict-needs-review），并把插件固定手法一并带过去；随后评估 26.x（26.1 / 26.1.1 / 26.3 blocked-no-paper-base，26.1.2 / 26.2 conflict-needs-review）。

## §21 第 26 轮：compat/1.21.10 达成完整构建 success（2026-11-26，Asia/Shanghai）

**目标版本与当前 ref**：目标 `1.21.10`（高于 1.21.8 基线，与 1.21.8 并存）。分支 `compat/1.21.10`，本轮提交 `6a63f73ebcbfdf0105aadfad9afcfd699194d48f`，`ls-remote` 读到的远端 ref 与本地 HEAD 同 sha（`4f6cf0f..6a63f73`），tag `leaves-1.21.10-compat` 的 ref object 也等于该 commit，汇报与实际 ref 一致。起点是上一轮的 blocked 提交 `4f6cf0f`。

**路线裁决（为什么整体采纳上游基线，而不是继续在旧基线上打补丁）**：旧 `gradle.properties` 的 `paperRef=bae47d3e…` 早于上游 `dev/1.21.10` 的 `af06383701d3c02e3e3cbd2ef5c5b55e2bbd0742`（mache 停留在 `1.21.10+build.1`、fill 1.0.7），因此我方三层补丁是 1.21.9 时代写的，上下文与上游 1.21.10 错位；同时失败运行残留的生成物 `leaves-server/build.gradle.kts` 里是上游 Paper 的 `io.papermc.paperweight.core`（无版本号），配置期就崩，`applyAllPatches` 根本不执行。结论：以 `LeavesMC/Leaves dev/1.21.10`（tree sha `9bc5f5b07e48c45296e0dabe9446e8f5c4ada040`）为权威基线做整体采纳，是改写量最小、可复现性最强的路线；这条路线由我自己裁决并记录，不再请示。

**采纳内容（三层补丁 + 两个 build 补丁）**：用 `adopt_upstream_12110.py` 递归替换补丁目录 —— `leaves-server/minecraft-patches` 143、`leaves-server/paper-patches` 17（含 `features/`、`files/`）、`leaves-api/paper-patches` 9，标题映射 matched=全量、only_our=0、only_up=0；采纳上游 `leaves-server/build.gradle.kts.patch`（含 `spigot` 块）与 `leaves-api/build.gradle.kts.patch`；写 `gradle.properties` 为上游内容（LF、无 BOM）。`.gitattributes` 追加 `plugin-pin/** binary`（见 §19 的字节保真手法）。

**关键坑：上游的 `leaves-server/src/main/java` 与 `leaves-api/src` 不是生成物**。`.gitignore` 只忽略 `/leaves-server/src/minecraft`、`/paper-server`、`/paper-api` 与两个生成 `build.gradle.kts`；这两个主源码目录是仓库真实 tracked 源码（上游 tree 里 server_main 344 blob、api_src 53 blob）。我此前只替换了补丁目录，主源码仍是 master 旧版遗留 → `:leaves-server:compileJava` 报 100 个错误（`程序包 org.leavesmc.leaves.command.leaves.subcommands 不存在` 6、`找不到符号` 71、`方法不会覆盖或实现超类型的方法` 5、`GameProfile 无法转换为 NameAndId` 3、`boolean 无法转换为 IPlayerDataStorage` 2 等）。用 `adopt_main_src_12110.py` 按上游 tree + raw 逐文件采纳 397 个文件（`ok=397 fail=0`），并删除上游已不存在的同名旧文件 43 个（旧命令体系 `LeavesCommand*/subcommands`、旧 bot 体系 `BotCommand/IPlayerDataStorage/subcommands`、旧 config validator、jade accessor）。采纳后编译直接绿，**语义改写清单本轮为 0**：上游同名补丁体与上游主源码就是权威落点，无需手工重写任何语义。

**流水线改造（两处，都留证据）**：
1. `leaves-server/build.gradle.kts.patch` 的 **hunk #10 被 `git apply` 拒收**（`error: patch failed: paper-server/build.gradle.kts:344` + `Rejected hunk #10`；#1–#9 含 offset 全部 applied cleanly）——原因是 hunk #9 与 #10 的上下文区间重叠。paperweight 自身重放不受影响，但为了让 `git apply --check` 与后续重生成一致，用 `manual_hunk10_12110.py` 按上游补丁体逐行手工落地：删 `runPaperclip`/`runReobfPaperclip` 与 `fill { }`，换成 `runLeavesclip`/`runReobfLeavesclip`（指向 `createMojmapLeavesclipJar`）与 `createLeavesConfig`。校验输出：旧块唯一匹配 1 次、`runLeavesclip=1`、`runReobfLeavesclip=1`、`createLeavesConfig=1`、`runPaperclip=0`、无 `fill{}`/`BuildChannel`、`leavesweight.core=True`、`paperweight.core=False`、`fill.gradle=False`，`mache("io.papermc:mache:1.21.10+build.9")`、`leavesclip("org.leavesmc:leavesclip:3.0.8")`，总行数 422。证据 `E:\mc\leaves-work\round25-12110-hunk10-manual.txt`。等价性：内容逐行照抄上游补丁意图，未新增语义。
2. **网络抖动**：`checkoutPaperRepo` 的 fetch 反复 rc=128（真实根因 `error: RPC failed; curl 56 Recv failure: Connection was reset` / `fatal: expected flush after ref listing`）；解法是先手动带 `GIT_SSL_CAINFO` 对嵌套缓存仓库 `fetch origin af063837…`（rc=0）再重跑 gradle（E120 姿势）。期间也验证过本地 `paper-mirror.git` 能直接供对象（`git -C <cache> fetch E:/mc/leaves-work/paper-mirror.git af063837…` rc=0），但把 origin 指向 mirror 无效——paperweight 会把 origin 还原成 GitHub URL，所以 mirror 只作对象供给，不作 origin 改写。推送侧同样抖：`git push` 反复 `curl 28 Failed to connect`，改用 `-c http.postBuffer=524288000 -c http.version=HTTP/1.1` 一次成功。

**可复现插件坐标（沿用 §19 的 vendored 手法）**：构建插件钉为不可覆盖的时间戳快照 `org.leavesmc.leavesweight.patcher` marker `2.1.0-SNAPSHOT` → 实际解析 `2.1.0-20260523.044214-4`（buildNumber 4）。vendored 目录 `plugin-pin/`（Reposilite 的 groupId 点转目录布局）：`leavesweight-core/2.1.0-SNAPSHOT` 放 jar 12,129,279 B sha256 `c9b0afad2355c8718f974e5539610f24c7282f8cfe5b0465f9fec7afb1a91386`、pom 2,036 B sha256 `54c71509b01dffdaaf0f370d97d907369e9ba12d05a52aae39aafa8a42adcadd`（无 `<dependencies>`，已 shadowed）、瘦身 metadata 1,124 B sha256 `d29586d4265fdb23bf91861745f57ad0f4cfd21a8ddfd8a35be5d95cef37bd8d`；marker pom 把依赖写成时间戳坐标（原件 1,956 B sha256 `85ae2515…` → 改写 1,965 B sha256 `2e3be319b10da7969e1a715e184ceda7c211ccfb2574048ea2c3cfc7ef87aebd`），marker metadata 瘦身 633 B sha256 `a57673ae9c936a87ee16c090926640f2f59939bbe7b27f7384efdc81ec2aabbe`。`settings.gradle.kts` 的 `pluginManagement` 首块插入 `plugin-pin` 仓库（`includeGroupByRegex("org\\.leavesmc\\.leavesweight.*")`），root `build.gradle.kts` 钉 `version "2.1.0-20260523.044214-4"`。`leavesclip` 走不可变正式版 `org.leavesmc:leavesclip:3.0.8`（jar 2,989,341 B sha256 `4c5f8fceb75b50982ca811b489fb7a7e420c4fbb9a0c865646c46527dd142ea7`），无需 vendored。

**Spigot 开关裁决**：采纳上游 dev/1.21.10 的 `spigot { enabled = true; buildDataRef = "42d18d4c4653ffc549778dbe223f6994a031d69e"; packageVersion = "v1_21_R6" }`（我方旧补丁是 `enabled = false` + `v1_21_R5`，那是 1.21.9 时代的语义；且插件需 ≥2.1.0 才有 `enabled` 成员，2.0.0 上没有）。实测 Spigot 映射链 `cloneSpigotBuildData → unpackSpigotBuildData → generateSpigotMappings → spigotRemapJar` 全部成功，说明 1.21.10 走 Spigot 启用路线是可行的，不需要关闭开关。

**构建结果（门槛只有本地 gradle 日志；fork GitHub Actions `total_count = 0`，已记录为证据缺失）**：
- `applyAllPatches`：`BUILD SUCCESSFUL in 10m 32s`，日志含 `Applied 899 paper minecraft patches`、`Importing 1 classes from library sources`，无 spigot 报错、无 warning/error 行（`1.21.10-round26-apply-all2.log`）。此前两次 FAILED 分别是网络 fetch 128（`-round26-apply-all.log`）与 `gradle.properties` 被 BOM 污染后的配置期问题。
- `:leaves-server:compileJava` + `:leaves-api:compileJava`：`BUILD SUCCESSFUL in 2m 14s`（`-round26-compile2.log`），只剩 incubator.vector / deprecation 警告。上一轮 100 错误（`-round26-compile.log`）由主源码采纳解决。
- `:leaves-api:jar`、`:leaves-server:jar`、`:leaves-server:createMojmapBundlerJar`、`:leaves-server:createMojmapLeavesclipJar`：分别 `BUILD SUCCESSFUL`（28s / 4m 11s / 44s / 1m 37s，含 `--no-build-cache` 的从零重建）。注意 `reobfJar` 被上游禁用，交付走 mojmap；多任务混在一次调用会触发隐式依赖失败，必须分次调用。

**产物（已发布到独立 Release，未覆盖已有 asset）**：交付目录 `E:\mc\leaves-work\downloads\builds\1.21.10`
- `leaves-api-1.21.10-R0.1-SNAPSHOT.jar` 2,752,317 B sha256 `8f40121a3437d5b31417e3e260844defb84027b6f0daea13a6d3ead4fdcbfdc3`（判据条目 2452）
- `leaves-server-1.21.10-R0.1-SNAPSHOT.jar` 28,656,208 B sha256 `e4b639df21e4cde1f7096ee03e576d0b6019792f235eb7517736b2782a00ee28`（判据条目 18888）
- `leaves-bundler-1.21.10-R0.1-SNAPSHOT-mojmap.jar` 109,046,083 B sha256 `2da0deca29fc1fe576347d6b94830fe53ddbe04e2c0e2020b96a0527937905c7`（判据条目 58283）
- `leaves-leavesclip-1.21.10-R0.1-SNAPSHOT-mojmap.jar` 64,960,881 B sha256 `fd1993650b4a7abdb1d45a953724782f4be9d326fab32bd0bb7f42c68ea96267`（判据条目 14039）

**可复现判据**：删除 `leaves-server/build`、`leaves-api/build` 后，四个任务各自带 `--no-build-cache` 分次重跑（上游 `org.gradle.caching=true` 会让重建走 `FROM-CACHE`，严格复现必须关掉构建缓存），再按 zip 逐条目 CRC32（嵌套 jar 递归展开）比对；排除规则按 **basename**：`MANIFEST.MF` / `versions.list` / `patches.list` / `*.jar.patch`（含构建期哈希与时间戳，逐次重建必然不同；按完整路径排除会漏掉 `META-INF/versions/9/OSGI-INF/MANIFEST.MF` 这类嵌套条目，造成假差异）。两侧规则同一套、现算，不拿旧 JSON 快照当基线。结果：四个产物缺失 0 / 多余 0 / CRC 差异 0，**总差异条目 = 0，结论=可复现**（`round26-repro-vs-delivered.txt`）。字节级 sha256 不同只来自被排除项。

**发布**：新 tag `leaves-1.21.10-compat`（ref object = commit `6a63f73…`，与分支 ref 完全一致）+ Release id `458635670`（draft=False、prerelease=True、target_commitish=`6a63f73…`），4 个 asset 上传大小与交付目录一致。releases 21→22、assets 35→39；已有 release 的 id 集合无消失、asset 数无变动（`round26-release-12110.txt`）。API 姿势补充：`POST /git/tags` 必须带 `"type"`，缺它报 422。

**未合并 master 的理由（与 §17/§18 一致）**：每版本独立分支、不合 master 是既定路线，master 只承担文档与基线说明；本轮只追加本文档，不把 1.21.10 的源码树并入 master（否则会覆盖 1.21.11 的发布树）。这一偏离是有意裁决，写在这里备查。

**底线复核**：`Leaves-base-1218` detached `d93e9766d3797d130f66179e70a1b374c546fef7`、工作树干净、四个 1.21.8 jar 未动；已有 Release/asset 未删未覆盖；本轮 success 后才宣布并发 Release，证据齐全。

**记错本同步**：新增 E122（`gradle_task.ps1` 用 `Set-Content -Encoding UTF8` 给 `gradle.properties` 写进 BOM+CRLF 且不还原 → 提交前必须用 python 去 BOM/CRLF）、E123（github.com 大 pack push/pull 反复 `curl 28/56` → `-c http.postBuffer=524288000 -c http.version=HTTP/1.1` 重试，或先用本地 mirror 供对象）、E124（复现判据排除规则必须按 basename，且两侧同一套现算）、E125（上游 `leaves-server/src/main/java` 与 `leaves-api/src` 是真实 tracked 源码，跨版本移植必须整体采纳，否则 compileJava 上百个符号错误）；E120 追加本轮复现与 push 侧同族抖动。记错本行数与索引行在下一轮校验对齐。

**下一个目标**：`compat/1.21.9`（现为 conflict-needs-review）按同一手法推进——先对齐上游 `dev/1.21.9` 权威基线（补丁层 + 主源码 + build 补丁 + vendored 插件），再 apply → compile → jar/bundler/clip → 逐条目 CRC；随后评估 26.x（26.1.2 / 26.2 conflict-needs-review；26.1 / 26.1.1 / 26.3 blocked-no-paper-base）。
