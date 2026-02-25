plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.openapi.generator") version "7.10.0"
}

group = "com.sc7258"
version = "0.2.0"
description = "spring-shop-flyway"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.github.cdimascio:java-dotenv:5.2.2") // .env 로드
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Database Migration
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test") // Security Test 추가
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// OpenAPI Generator 설정
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/openapi/openapi.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("com.sc7258.springshopflyway.api")
    modelPackage.set("com.sc7258.springshopflyway.model")
    configOptions.set(mapOf(
        "interfaceOnly" to "false",
        "delegatePattern" to "true",
        "useTags" to "true",
        "useSpringBoot3" to "true",
        "gradleBuildFile" to "false",
        "enumPropertyNaming" to "UPPERCASE"
    ))
    globalProperties.set(mapOf(
        "models" to "",
        "apis" to "",
        "supportingFiles" to "ApiUtil.kt"
    ))
}

// 생성된 코드를 소스셋에 추가
sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory.get()}/generated/src/main/kotlin")
        }
    }
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

// openapi.yaml 파일을 static 리소스로 복사
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // 중복 시 덮어쓰기
    from("$rootDir/openapi/openapi.yaml") {
        into("static/api/v1") // 경로 변경
    }
}
