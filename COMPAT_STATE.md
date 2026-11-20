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
| 1.21.9 | `compat/1.21.9` | pending | 未开始 |
| 1.21.10 | `compat/1.21.10` | pending | 未开始 |
| 1.21.11 | `compat/1.21.11` | pending | 未开始 |
| 26.1 | `compat/26.1` | pending | Paper 无精确基线，见 §3 |
| 26.1.1 | `compat/26.1.1` | pending | 未开始 |
| 26.1.2 | `compat/26.1.2` | pending | 未开始 |
| 26.2 | `compat/26.2` | pending | 未开始 |
| 26.3 | `compat/26.3` | pending | 未开始 |

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
