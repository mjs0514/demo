pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // 서브모듈에서 개별 repositories 선언 금지
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "demo"

val excludedProjectDirs = setOf(
    "build-logic", // includeBuild로 따로 관리
)

rootDir.listFiles()
    ?.asSequence()
    ?.filter { it.isDirectory }
    ?.filterNot { it.name.startsWith(".") }
    ?.filterNot { it.name in excludedProjectDirs }
    ?.filter { it.resolve("build.gradle.kts").isFile }
    ?.sortedBy { it.name }
    ?.forEach { dir ->
        val projectPath = ":${rootProject.name}-${dir.name}"

        include(projectPath)
        project(projectPath).projectDir = dir
    }