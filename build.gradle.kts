plugins {
    java
    id("org.springframework.boot") version "3.5.16" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// 모든 프로젝트(루트 포함) 공통 설정
allprojects {
    group = "com.tmax"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// 모든 서브 모듈(apps, core 등)에만 공통 적용
subprojects {
    plugins.apply("java")
    plugins.apply("io.spring.dependency-management")

    dependencies {
        // 공통 컴파일/테스트 라이브러리 (Lombok, JUnit)
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}