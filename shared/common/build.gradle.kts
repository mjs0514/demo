plugins {
    id("common-java")
    id("common-spotless")
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString())
    }
}

dependencies {
    implementation(libs.spring.boot.starter)
}