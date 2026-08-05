plugins {
    base
    alias(libs.plugins.spotless) apply false
}

tasks.register("generateDbScripts") {
    group = "database"
    description = "Generate merged DB scripts per vendor and module"

    doLast {
        fun sql(dir: File, name: String) = File(dir, name).takeIf(File::exists)

        fun versionKey(name: String): List<Int> =
            name.removePrefix("V")
                .removeSuffix(".sql")
                .split('.')
                .map { it.toIntOrNull() ?: Int.MAX_VALUE }

        fun compareVersion(a: String, b: String): Int {
            val left = versionKey(a)
            val right = versionKey(b)
            left.zip(right).firstOrNull { it.first != it.second }?.let { return it.first - it.second }
            return left.size - right.size
        }

        fun migrationFiles(dir: File): List<File> {
            val migrationDir = File(dir, "migration")
            if (!migrationDir.isDirectory) return emptyList()
            return migrationDir.listFiles()
                ?.filter { it.isFile && it.extension == "sql" }
                ?.sortedWith(Comparator { a, b -> compareVersion(a.name, b.name).takeIf { it != 0 } ?: a.name.compareTo(b.name) })
                ?: emptyList()
        }

        fun modules(dir: File): List<File> =
            dir.listFiles()
                ?.filter { it.isDirectory && it.name != "common" }
                ?.sortedBy { it.name }
                ?: emptyList()

        fun writeMerged(out: File, title: String, files: List<File>) {
            out.writeText(buildString {
                appendLine("-- $title")
                appendLine()
                files.forEach {
                    appendLine("-- File: ${it.parentFile.name}/${it.name}")
                    appendLine(it.readText())
                    appendLine()
                }
            })
        }

        fun writeChanges(out: File, vendor: String, commonDir: File, vendorModules: List<File>) {
            val grouped = linkedMapOf<String, MutableList<Pair<String, File>>>()

            migrationFiles(commonDir).forEach { grouped.getOrPut(it.name) { mutableListOf() }.add("common" to it) }
            vendorModules.forEach { module ->
                migrationFiles(module).forEach { grouped.getOrPut(it.name) { mutableListOf() }.add(module.name to it) }
            }

            out.writeText(buildString {
                appendLine("-- CHANGES FOR $vendor")
                appendLine()
                grouped.toSortedMap(Comparator(::compareVersion)).forEach { (version, files) ->
                    appendLine("-- Migration: $version")
                    files.forEach { (module, file) ->
                        appendLine("-- Module: $module")
                        appendLine(file.readText())
                        appendLine()
                    }
                }
            })
        }

        val dbRoot = file("db")
        val vendorDirs = dbRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?: emptyList()

        vendorDirs.forEach { vendorDir ->
            val outputDir = layout.buildDirectory.dir("generated-db/${vendorDir.name}").get().asFile
            val commonDir = File(vendorDir, "common")
            val vendorModules = modules(vendorDir)

            outputDir.mkdirs()
            outputDir.listFiles()?.forEach { it.delete() }

            writeMerged(
                File(outputDir, "schema.sql"),
                "SCHEMA FOR ${vendorDir.name}",
                listOfNotNull(sql(commonDir, "schema.sql")) + vendorModules.mapNotNull { sql(it, "schema.sql") }
            )

            writeMerged(
                File(outputDir, "data.sql"),
                "DATA FOR ${vendorDir.name}",
                listOfNotNull(sql(commonDir, "data.sql")) + vendorModules.mapNotNull { sql(it, "data.sql") }
            )

            writeChanges(File(outputDir, "changes.sql"), vendorDir.name, commonDir, vendorModules)
        }
    }
}

val packageDistribution = tasks.register<Zip>("packageDistribution") {
    group = "distribution"
    description = "Package apps bootJar, scripts, and generated DB files into a ZIP"

    dependsOn("generateDbScripts")
    dependsOn(":apps:bootJar")

    archiveBaseName.set(rootProject.name)
    archiveVersion.set(project.version.toString())
    archiveExtension.set("zip")

    into("${rootProject.name}-${project.version}") {
        from("script") {
            into("bin")
            filePermissions {
                user {
                    read = true
                    write = true
                    execute = true
                }
                group {
                    read = true
                    execute = true
                }
                other {
                    read = true
                    execute = true
                }
            }
        }

        from(project(":apps").tasks.named("bootJar")) {
            into("libs")
        }

        from(layout.buildDirectory.dir("generated-db")) {
            into("db")
        }
    }
}

tasks.build {
    dependsOn(packageDistribution)
}
