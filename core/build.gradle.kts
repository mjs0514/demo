description = "core"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.16")
    }
}

dependencies {
    // 순수 Spring 기능 및 DB/JPA 관련 의존성 (필요 시 추가)
    implementation("org.springframework.boot:spring-boot-starter")
}