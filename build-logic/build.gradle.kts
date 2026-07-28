plugins {
    `kotlin-dsl`
}

dependencies {
    // build-logic 내부의 사전 컴파일된 플러그인들이 스프링 플러그인을 인식할 수 있도록 주입
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management.gradle.plugin)
}