package com.sc7258.springshopflyway

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringShopFlywayApplication

fun main(args: Array<String>) {
    // 1. 활성 프로파일 확인 (시스템 프로퍼티 또는 환경변수)
    val activeProfile = System.getProperty("spring.profiles.active") 
        ?: System.getenv("SPRING_PROFILES_ACTIVE") 
        ?: "local" // 기본값

    // 2. 프로파일에 맞는 .env 파일명 결정
    val envFileName = when (activeProfile) {
        "prod" -> ".env.prod"
        "dev" -> ".env.dev"
        else -> ".env.local"
    }

    // 3. 해당 .env 파일 로드 (있을 경우에만)
    try {
        val dotenv = Dotenv.configure()
            .filename(envFileName) // 파일명 지정
            .ignoreIfMissing()
            .load()
            
        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }
        println("Loaded environment variables from: $envFileName")
    } catch (e: Exception) {
        // 파일이 없거나 로드 실패 시 무시 (운영 환경 등에서는 환경변수 직접 사용 가능)
        println("Skipped loading environment variables from: $envFileName (File not found or error)")
    }
    
    runApplication<SpringShopFlywayApplication>(*args)
}
