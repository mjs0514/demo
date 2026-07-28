plugins {
    base
}

// 2. 배포용 ZIP 패키징 태스크 정의 (tasks.register<Zip> 이용)
val packageDistribution = tasks.register<Zip>("packageDistribution") {
    group = "distribution"
    description = "apps의 bootJar와 bin 디렉토리의 실행 스크립트를 묶어 배포용 ZIP을 생성합니다."

    // apps 모듈의 bootJar 빌드가 완료된 후 실행
    dependsOn(":apps:bootJar")

    archiveBaseName.set(rootProject.name)
    archiveVersion.set(project.version.toString())
    archiveExtension.set("zip")

    into("${rootProject.name}-${project.version}") {

        // ① bin/ 디렉토리 내 스크립트 포함 및 실행 권한(rwxr-xr-x) 설정
        from("script") {
            into("bin")
            filePermissions {
                user {
                    read = true; write = true; execute = true
                }
                group {
                    read = true; execute = true
                }
                other {
                    read = true; execute = true
                }
            }
        }

        // ② apps 모듈에서 생성된 bootJar 파일만 libs/ 디렉토리에 포함
        from(project(":apps").tasks.named("bootJar")) {
            into("libs")
        }
    }
}

// 3. root의 build 태스크 실행 시 Zip 패키징이 자동으로 함께 수행되도록 연결
tasks.build {
    dependsOn(packageDistribution)
}