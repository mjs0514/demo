plugins {
    id("common-java")
    alias(libs.plugins.spring.dependency.management)
}

description = "core 서비스들을 가지는 프로젝트"

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString())
    }
}

dependencies {
    // 순수 Spring 기능 (DB/JPA 관련 의존성 필요 시 추가)
    implementation(libs.spring.boot.starter)
    implementation(project(":shared:common"))
}