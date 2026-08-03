plugins {
    java
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    // querydsl
    implementation(libs.findLibrary("querydsl-core").get())
    implementation(libs.findLibrary("querydsl-jpa").get())

    // 2. JPA 및 Annotation API (compileOnly 또는 implementation으로 변경!)
    // compileOnly: 이 플러그인을 쓰는 모듈에서 @Entity, @Table, @Id 등을 작성할 수 있게 해줍니다.
    // (Spring Boot Data JPA 스타터를 쓰는 모듈이라면 implementation 대신 compileOnly로도 충분합니다)
    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("jakarta.annotation:jakarta.annotation-api")

    annotationProcessor(libs.findLibrary("querydsl-apt").get()) {
        artifact {
            classifier = "jakarta"
        }
    }
}

// QClass 출력 디렉토리 지정
val querydslDir = layout.buildDirectory.dir("generated/querydsl")

tasks.withType<JavaCompile>().configureEach {
    // JavaCompile 타스크 실행 시 QClass 생성 경로 설정
    // (Gradle이 이 디렉토리를 main sourceSet에 자동으로 포함시킵니다)
    options.generatedSourceOutputDirectory.set(querydslDir)
}