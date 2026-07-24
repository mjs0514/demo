plugins {
    id("org.springframework.boot")
}

description = "apps"

dependencies {
    // 1. core 모듈을 의존성으로 추가 (core의 Service, Domain 접근 가능)
    implementation(project(":core"))

    // 2. Web MVC 컨트롤러 및 RestController 구동용 스타터
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// apps 모듈은 단독 실행 가능한 Fat JAR로 빌드됨
tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}