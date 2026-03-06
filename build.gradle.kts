import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property
import org.jooq.meta.jaxb.Target

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.openapi.generator") version "7.10.0"
    id("org.jooq.jooq-codegen-gradle") version "3.19.18"
}

group = "com.sc7258"
version = "0.2.0"
description = "spring-shop-flyway"

val jooqVersion = "3.19.18"
extra["jooq.version"] = jooqVersion

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
    implementation("org.springframework.boot:spring-boot-starter-jooq")
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
    
    // Swagger / OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Database Migration
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    jooqCodegen("org.mariadb.jdbc:mariadb-java-client")
    jooqCodegen("org.jooq:jooq-meta-extensions:$jooqVersion")
    
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

jooq {
    configuration {
        logging = Logging.WARN
        generator.apply {
            name = "org.jooq.codegen.KotlinGenerator"
            database =
                Database()
                    .withName("org.jooq.meta.extensions.ddl.DDLDatabase")
                    .withInputSchema("PUBLIC")
                    .withIncludes(".*")
                    .withExcludes("flyway_schema_history")
                    .withProperties(
                        Property().withKey("scripts").withValue("src/main/resources/db/migration/*.sql"),
                        Property().withKey("sort").withValue("flyway"),
                    )
            generate =
                Generate()
                    .withDeprecated(false)
                    .withRecords(true)
                    .withImmutablePojos(false)
                    .withFluentSetters(false)
                    .withDaos(false)
                    .withPojos(false)
                    .withKotlinNotNullPojoAttributes(true)
                    .withKotlinNotNullRecordAttributes(true)
            target =
                Target()
                    .withPackageName("com.sc7258.springshopflyway.jooq.generated")
                    .withDirectory("${layout.buildDirectory.get()}/generated-src/jooq/main")
        }
    }
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
            srcDir("${layout.buildDirectory.get()}/generated-src/jooq/main")
        }
    }
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
    dependsOn(tasks.named("jooqCodegen"))
}

// openapi.yaml 파일을 static 리소스로 복사
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // 중복 시 덮어쓰기
    from("$rootDir/openapi/openapi.yaml") {
        into("static/api/v1") // 경로 변경
    }
}
