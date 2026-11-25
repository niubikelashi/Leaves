pluginManagement {
    repositories {
        // Leaves - 插件字节固定：vendored 本地仓库，坐标是不可覆盖的时间戳快照（build 4）
        maven(url = settingsDir.resolve("plugin-pin").toURI()) {
            name = "plugin-pin"
            content {
                includeGroupByRegex("org\\.leavesmc\\.leavesweight.*")
            }
        }
        gradlePluginPortal()
        maven("https://repo.leavesmc.org/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.9.0")
}

rootProject.name = "Leaves"

include("leaves-api", "leaves-server")
