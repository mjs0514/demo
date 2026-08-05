import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

configure<SpotlessExtension> {
    java {
        target("**/*.java")
        targetExclude(
            "**/model/wildfly/**",
            "**/build/**",
            "**/generated/**"
        )

        //licenseHeaderFile("${project.rootDir}/license.header").updateYearWithLatest(true)

        palantirJavaFormat("2.85.0")
        trimTrailingWhitespace()
        endWithNewline()
        suppressLintsFor {
            step = "palantir-java-format"
        }
    }
}

plugins.withId("java") {
    tasks.named("check") {
        dependsOn("spotlessCheck")
    }
}