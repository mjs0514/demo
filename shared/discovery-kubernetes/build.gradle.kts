plugins {
    id("common-java")
}

dependencies {
    // api vs implementation 의논하기
    // autoconfigure 에서 한번에 사용할거면 api, 그렇지 않다면 implementation
    implementation(libs.fabric8.kubernetes.client)
}