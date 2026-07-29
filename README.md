# demo

Gradle 멀티 모듈 프로젝트의 기본 구조를 실험하기 위한 데모 프로젝트입니다.

이 프로젝트의 목적은 소스 코드는 모듈별로 독립적으로 분리하되, 공통 설정과 의존성 버전 관리는 한 곳에서 관리하여 유지보수성과 빌드 효율을 높이는 구조를 찾는 것입니다. 루트 프로젝트는 애플리케이션 코드를 직접 빌드하지 않고 distribution 패키징만 담당하며, 서브 프로젝트들이 공통으로 사용하는 빌드 설정은 `build-logic`의 convention plugin으로 분리합니다.

## 설계 방향

- 루트 프로젝트는 배포 산출물 조립만 담당합니다.
- 실제 소스와 의존성은 `demo-apps`, `demo-core` 같은 서브 프로젝트에서 관리합니다.
- 공통 Java/Spring Boot 빌드 설정은 `build-logic`의 precompiled convention plugin으로 관리합니다.
- 프로젝트 공통 프로퍼티는 `gradle.properties`에서 관리합니다.
- 의존성 및 플러그인 버전은 `gradle/libs.versions.toml`에서 중앙 관리합니다.
- `subprojects { ... }`에 많은 설정을 밀어 넣기보다, 필요한 모듈이 필요한 convention plugin을 명시적으로 적용하는 구조를 사용합니다.

## 디렉토리 구조

```text
.
|-- apps/
|-- core/
|-- build-logic/
|   `-- src/main/kotlin/
|       |-- common-java.gradle.kts
|       `-- common-spring-boot.gradle.kts
|-- gradle/
|   |-- libs.versions.toml
|   `-- wrapper/
|-- script/
|-- build.gradle.kts
|-- settings.gradle.kts
|-- gradle.properties
|-- gradlew
`-- gradlew.bat
```

### Root Project

루트 프로젝트는 전체 멀티 모듈 빌드의 진입점입니다.

- `settings.gradle.kts`
  - 프로젝트 이름과 서브 모듈을 정의합니다.
  - 서브 모듈은 `${rootProject.name}-${subDir.name}` 라는 이름으로 자동 include 됩니다.
  - `includeBuild("build-logic")`를 통해 convention plugin을 사용할 수 있게 합니다.
  - 전체 프로젝트의 repository 정책을 정의합니다.

- `build.gradle.kts`
  - `base` 플러그인을 사용하여 루트 프로젝트를 distribution 중심 프로젝트로 유지합니다.
  - `packageDistribution` 태스크를 통해 `demo-apps`의 `bootJar`와 `script` 디렉토리의 실행 스크립트를 ZIP으로 묶습니다.
  - 루트 `build` 실행 시 distribution 패키지가 함께 만들어지도록 연결합니다.

- `gradle.properties`
  - 전체 프로젝트에서 공유하는 Gradle 프로퍼티를 관리합니다.
  - 현재는 프로젝트 버전인 `version=21.1.0`을 정의합니다.

### `gradle/`

Gradle wrapper와 버전 카탈로그를 관리하는 디렉토리입니다.

- `gradle/libs.versions.toml`
  - 외부 라이브러리 버전, Gradle 플러그인 버전, 공통 dependency alias를 중앙 관리합니다.
  - Spring Boot, Spring Dependency Management 플러그인, Spring Boot starter 의존성 등을 이 파일에서 관리합니다.
  - 새 외부 라이브러리를 추가할 때는 먼저 이 파일에 alias를 추가한 뒤 각 모듈에서 `libs.xxx` 형태로 참조하는 것을 기본 원칙으로 합니다.

### `build-logic/`

서브 프로젝트들이 공유하는 Gradle convention plugin을 관리하는 included build입니다.

`build-logic`을 별도 빌드로 분리하면 공통 빌드 로직을 재사용하기 쉽고, Gradle의 증분 빌드와 캐시를 활용하기 좋은 구조를 만들 수 있습니다.

- `build-logic/settings.gradle.kts`
  - `build-logic` 자체의 dependency repository를 정의합니다.
  - 루트의 `gradle/libs.versions.toml`을 가져와 convention plugin 빌드에서도 동일한 버전 카탈로그를 사용합니다.

- `build-logic/build.gradle.kts`
  - `kotlin-dsl` 플러그인을 적용하여 `.gradle.kts` precompiled convention plugin을 작성할 수 있게 합니다.
  - convention plugin 내부에서 사용할 Spring Boot Gradle Plugin, Spring Dependency Management Plugin을 classpath에 추가합니다.

### `apps/`

실행 가능한 Spring Boot 애플리케이션 모듈입니다.

- `common-java`를 적용하여 Java 공통 설정을 사용합니다.
- `common-spring-boot`를 적용하여 Spring Boot 애플리케이션 빌드 설정을 사용합니다.
- `demo-core` 모듈을 의존하여 서비스/도메인 로직을 사용합니다.
- `bootJar`가 최종 실행 가능한 애플리케이션 JAR를 생성합니다.
- 일반 `jar`는 비활성화하여 plain jar가 별도로 생성되지 않도록 합니다.

### `core/`

공통 서비스와 도메인 로직을 담는 라이브러리 모듈입니다.

- `common-java`를 적용하여 Java 공통 설정을 사용합니다.
- Spring Boot 애플리케이션 모듈은 아니므로 `common-spring-boot`는 적용하지 않습니다.
- Spring dependency management 플러그인과 Spring Boot BOM은 모듈에서 직접 적용합니다.
- 일반 Java library 형태의 JAR를 생성합니다.

### `script/`

distribution 패키지에 포함할 실행 스크립트를 관리합니다.

루트의 `packageDistribution` 태스크는 이 디렉토리의 파일을 ZIP 내부의 `bin/` 디렉토리로 복사하고 실행 권한을 부여합니다.

## Convention Plugins

### `common-java`

파일: `build-logic/src/main/kotlin/common-java.gradle.kts`

Java 기반 모듈에서 공통으로 사용하는 설정을 제공합니다.

현재 포함된 내용:

- `java` 플러그인 적용
- `group = "com.tmax"` 설정
- Java toolchain JDK 17 설정
- Lombok 컴파일 의존성 추가
- JUnit Platform launcher 테스트 런타임 의존성 추가
- 모든 `Jar` 계열 태스크에 공통 manifest 속성 추가
- 모든 `Test` 태스크에 `useJUnitPlatform()` 적용

이 플러그인에 추가하면 좋은 내용:

- 모든 Java 모듈에 동일하게 필요한 컴파일 옵션
- 모든 Java 모듈에 필요한 annotation processor
- 모든 JAR 산출물에 들어가야 하는 manifest 속성
- 테스트 실행 방식, 테스트 로깅, 공통 JVM 옵션
- Java toolchain, encoding 같은 언어/컴파일 공통 정책

주의할 점:

- `tasks.withType<Jar>().configureEach`에서는 manifest처럼 모든 JAR에 붙일 설정만 관리합니다.
- 특정 JAR 태스크를 강제로 켜거나 끄는 `enabled` 설정은 여기 넣지 않는 것이 좋습니다.
- 애플리케이션 모듈과 라이브러리 모듈의 산출물 정책이 다를 수 있으므로 `jar`, `bootJar` 활성화 여부는 각 모듈 또는 전용 convention plugin에서 결정합니다.

### `common-spring-boot`

파일: `build-logic/src/main/kotlin/common-spring-boot.gradle.kts`

실행 가능한 Spring Boot 애플리케이션 모듈에서 사용하는 설정을 제공합니다.

현재 포함된 내용:

- `org.springframework.boot` 플러그인 적용
- `io.spring.dependency-management` 플러그인 적용

이 플러그인에 추가하면 좋은 내용:

- 모든 Spring Boot 애플리케이션에 공통으로 적용할 `bootJar` 설정
- Spring Boot 애플리케이션 공통 JVM 실행 옵션
- actuator, logging, configuration processor 등 애플리케이션 공통 의존성
- Boot 애플리케이션에만 필요한 packaging 정책

주의할 점:

- 라이브러리 모듈에는 이 플러그인을 적용하지 않습니다.
- `org.springframework.boot` 플러그인이 적용되면 일반 `jar`와 `bootJar` 산출물 정책이 Spring Boot 기준으로 바뀝니다.
- `demo-core`처럼 실행 애플리케이션이 아닌 모듈에 적용하면 `bootJar` 비활성화, plain jar classifier 조정 같은 보정이 필요해질 수 있습니다.

## 어디에 무엇을 추가할까?

| 추가하려는 내용 | 위치 |
| --- | --- |
| 전체 프로젝트 버전 | `gradle.properties` |
| 외부 라이브러리/플러그인 버전 | `gradle/libs.versions.toml` |
| 모든 Java 모듈 공통 설정 | `common-java.gradle.kts` |
| 모든 JAR manifest 공통 속성 | `common-java.gradle.kts` |
| Spring Boot 앱에만 필요한 설정 | `common-spring-boot.gradle.kts` |
| 실행 가능한 애플리케이션 의존성 | `apps/build.gradle.kts` |
| 도메인/서비스 라이브러리 의존성 | `core/build.gradle.kts` |
| 배포 ZIP 구성 | 루트 `build.gradle.kts` |
| 배포에 포함할 스크립트 | `script/` |

## 빌드 명령

루트 Gradle wrapper를 기준으로 실행합니다.

```bash
./gradlew build
```

주요 태스크:

```bash
./gradlew :demo-apps:bootJar
./gradlew :demo-core:jar
./gradlew packageDistribution
```

Windows 환경에서는 다음처럼 실행합니다.

```powershell
.\gradlew.bat build
.\gradlew.bat packageDistribution
```

## 유지보수 원칙

- 새 모듈을 추가할 때는 필요한 convention plugin만 명시적으로 적용합니다.
- Java 모듈이면 기본적으로 `common-java`를 적용합니다.
- 실행 가능한 Spring Boot 애플리케이션이면 `common-spring-boot`를 추가로 적용합니다.
- 라이브러리 모듈에는 Spring Boot 애플리케이션 플러그인을 적용하지 않습니다.
- 버전 문자열은 가능한 한 `gradle/libs.versions.toml`로 모읍니다.
- 루트 프로젝트에는 소스 빌드 로직을 두지 않고 distribution 조립 역할만 둡니다.
