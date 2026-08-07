plugins {
    base
    alias(libs.plugins.spotless) apply false
}

tasks.register("generateDbScripts") {
    group = "database"
    description = "Generate merged DB scripts per vendor from root and subprojects"

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

        fun migrationFiles(dir: File?): List<File> {
            if (dir == null) return emptyList()
            val migrationDir = File(dir, "migration")
            if (!migrationDir.isDirectory) return emptyList()
            return migrationDir.listFiles()
                ?.filter { it.isFile && it.extension == "sql" }
                ?.sortedWith(Comparator { a, b -> compareVersion(a.name, b.name).takeIf { it != 0 } ?: a.name.compareTo(b.name) })
                ?: emptyList()
        }

        fun writeMerged(out: File, title: String, files: List<File>) {
            out.writeText(buildString {
                appendLine("-- $title")
                appendLine()
                files.forEach {
                    appendLine("-- File: ${it.relativeTo(rootDir).path}")
                    appendLine(it.readText())
                    appendLine()
                }
            })
        }

        fun writeChanges(out: File, vendor: String, commonVendorDir: File?, vendorModuleDirs: List<Pair<String, File>>) {
            val grouped = linkedMapOf<String, MutableList<Pair<String, File>>>()

            // 1. 공통 영역(/db/$vendor/migration/) 마이그레이션 파일 추가
            migrationFiles(commonVendorDir).forEach { grouped.getOrPut(it.name) { mutableListOf() }.add("common" to it) }

            // 2. 각 모듈 영역(/$module/db/$vendor/migration/) 마이그레이션 파일 추가
            vendorModuleDirs.forEach { (moduleName, moduleVendorDir) ->
                migrationFiles(moduleVendorDir).forEach { grouped.getOrPut(it.name) { mutableListOf() }.add(moduleName to it) }
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

        // 1. 공통 DB 루트 디렉토리 (/db)
        val rootDbDir = file("db")

        // 2. 모든 DB Vendor 목록 동적 탐색 (/db/$vendor 및 서브프로젝트의 /$module/db/$vendor 수집)
        val allDbDirs = listOf(rootDbDir) + subprojects.map { it.file("db") }
        val vendors = allDbDirs.flatMap { dbDir ->
            dbDir.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
        }.distinct().sorted()

        // 3. Vendor별 스크립트 병합
        vendors.forEach { vendor ->
            val outputDir = layout.buildDirectory.dir("generated-db/$vendor").get().asFile

            // 공통 Vendor 디렉토리 (/db/$vendor)
            val commonVendorDir = File(rootDbDir, vendor).takeIf { it.isDirectory }

            // 모듈별 Vendor 디렉토리 목록 (/$module/db/$vendor)
            val vendorModuleDirs = subprojects
                .mapNotNull { subproject ->
                    val vDir = File(subproject.file("db"), vendor)
                    if (vDir.isDirectory) subproject.name to vDir else null
                }
                .sortedBy { it.first }

            outputDir.mkdirs()
            outputDir.listFiles()?.forEach { it.delete() }

            // schema.sql 병합 (/db/$vendor/schema.sql + /$module/db/$vendor/schema.sql)
            val schemaFiles = listOfNotNull(commonVendorDir?.let { sql(it, "schema.sql") }) +
                    vendorModuleDirs.mapNotNull { (_, dir) -> sql(dir, "schema.sql") }
            if (schemaFiles.isNotEmpty()) {
                writeMerged(File(outputDir, "schema.sql"), "SCHEMA FOR $vendor", schemaFiles)
            }

            // data.sql 병합 (/db/$vendor/data.sql + /$module/db/$vendor/data.sql)
            val dataFiles = listOfNotNull(commonVendorDir?.let { sql(it, "data.sql") }) +
                    vendorModuleDirs.mapNotNull { (_, dir) -> sql(dir, "data.sql") }
            if (dataFiles.isNotEmpty()) {
                writeMerged(File(outputDir, "data.sql"), "DATA FOR $vendor", dataFiles)
            }

            // changes.sql 생성 (/db/$vendor/migration/ + /$module/db/$vendor/migration/)
            writeChanges(File(outputDir, "changes.sql"), vendor, commonVendorDir, vendorModuleDirs)
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
