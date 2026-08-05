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
    implementation(libs.spring.boot.starter.autoconfigure)
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    compileOnly(project(":shared:discovery-kubernetes"))
    compileOnly(libs.fabric8.kubernetes.client)

}