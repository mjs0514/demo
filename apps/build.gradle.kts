plugins {
    id("common-java")
    id("common-spring-boot")
}

description = "spring boot application 을 만드는 프로젝트"

dependencies {
    // 1. core 모듈을 의존성으로 추가 (core의 Service, Domain 접근 가능)
    implementation(project(":core"))
    implementation(project(":shared:web"))

    // 2. Web MVC 컨트롤러 및 RestController 구동용 스타터
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}

// apps 모듈은 단독 실행 가능한 Fat JAR 만 빌드되면 됨
tasks.jar {
    enabled = false
}