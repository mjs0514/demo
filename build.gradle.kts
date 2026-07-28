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

tasks.jar {
    enabled = false // demo-0.0.1-SNAPSHOT.jar (빈 껍데기) 생성을 막음
}

// 2. 배포용 ZIP 패키징 태스크 정의 (tasks.register<Zip> 이용)
val packageDistribution = tasks.register<Zip>("packageDistribution") {
    group = "distribution"
    description = "apps의 bootJar와 bin 디렉토리의 실행 스크립트를 묶어 배포용 ZIP을 생성합니다."

    // apps 모듈의 bootJar 빌드가 완료된 후 실행
    dependsOn(":apps:bootJar")

    archiveBaseName.set(rootProject.name)
    archiveVersion.set(project.version.toString())
    archiveExtension.set("zip")

    into("${rootProject.name}-${project.version}") {

        // ① bin/ 디렉토리 내 스크립트 포함 및 실행 권한(rwxr-xr-x) 설정
        from("bin") {
            into("bin")
            filePermissions {
                user {
                    read = true; write = true; execute = true
                }
                group {
                    read = true; execute = true
                }
                other {
                    read = true; execute = true
                }
            }
        }

        // ② apps 모듈에서 생성된 bootJar 파일만 libs/ 디렉토리에 포함
        from(project(":apps").tasks.named("bootJar")) {
            into("libs")
        }
    }
}

// 3. root의 build 태스크 실행 시 Zip 패키징이 자동으로 함께 수행되도록 연결
tasks.build {
    dependsOn(packageDistribution)
}