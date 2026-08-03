plugins {
    java
    jacoco
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

// 💡 플러그인을 적용한 모듈에 JUnit 5 및 AssertJ 의존성을 자동 주입합니다.
dependencies {
    // junit-jupiter Aggregator (api, params, engine 등을 모두 포함)
    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())

    // Fluent Assertion을 위한 AssertJ
    testImplementation(libs.findLibrary("assertj-core").get())
}

// 1. gradle.properties에서 프로퍼티를 읽어와 바로 리스트로 변환합니다.
val jacocoExclusionPatterns: List<String> = (project.findProperty("jacocoExclusionPatterns") as? String)
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    // JVM & 메모리 옵션
    maxHeapSize = "1g"
    jvmArgs("-XX:MaxMetaspaceSize=256m")
    setForkEvery(150)

    // 태스크 실행 완료 후 레포트 자동 생성
    finalizedBy(tasks.jacocoTestReport)

    // 로깅 설정
    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT

        debug {
            events("started", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        info.events("failed", "skipped")
    }

    // 요약 리스너
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println("\n-------------------------------------------------------")
                println("Test result: ${result.resultType}")
                println("Test summary: ${result.testCount} tests, ${result.successfulTestCount} succeeded, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped")
                println("-------------------------------------------------------\n")
            }
        }
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
    })
}

tasks.jacocoTestReport {
    // Test 태스크 실행 보장
    dependsOn(tasks.withType<Test>())

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(jacocoExclusionPatterns)
            }
        })
    )
}