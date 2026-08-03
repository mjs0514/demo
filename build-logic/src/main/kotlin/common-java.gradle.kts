plugins {
    java
    id("common-test")
}

group="com.tmax"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    // 공통 컴파일/테스트 라이브러리 (Lombok, JUnit)
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}

// Jar 작업 설정 (Manifest 등 커스텀 로직 적용)
tasks.withType<Jar>().configureEach {
    // buildType 등 프로젝트 프로퍼티 기반으로 Manifest 설정
    val buildTypeVal = project.findProperty("buildType")?.toString() ?: "dev"

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Build-Type" to buildTypeVal,
            "Built-By" to "Tmax Soft"
        )
    }
    // 만약 커스텀 확장 함수 setupSfmManifest(...)가 있다면
    // build-logic 내의 Kotlin 파일/클래스로 정의하여 호출 가능합니다.
}

tasks.withType<Test> {
    useJUnitPlatform()
}
