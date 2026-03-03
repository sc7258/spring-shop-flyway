package com.sc7258.springshopflyway

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringShopFlywayApplication

fun main(args: Array<String>) {
    val activeProfile = resolveActiveProfile(
        args = args,
        systemProperty = System.getProperty("spring.profiles.active"),
        environmentVariable = System.getenv("SPRING_PROFILES_ACTIVE")
    )

    val envFileName = resolveEnvFileName(activeProfile)

    if (envFileName != null) {
        try {
            val dotenv = Dotenv.configure()
                .filename(envFileName)
                .ignoreIfMissing()
                .load()

            dotenv.entries().forEach { entry ->
                System.setProperty(entry.key, entry.value)
            }
            println("Loaded environment variables from: $envFileName")
        } catch (e: Exception) {
            println("Skipped loading environment variables from: $envFileName (File not found or error)")
        }
    } else {
        println("Skipped loading environment variables for profile: $activeProfile")
    }
    
    runApplication<SpringShopFlywayApplication>(*args)
}

internal fun resolveActiveProfile(
    args: Array<String>,
    systemProperty: String?,
    environmentVariable: String?
): String {
    return parseProfileFromArgs(args)
        ?: extractPrimaryProfile(systemProperty)
        ?: extractPrimaryProfile(environmentVariable)
        ?: "dev"
}

internal fun resolveEnvFileName(activeProfile: String): String? {
    return when (activeProfile) {
        "dev" -> ".env.dev"
        "qa" -> ".env.qa"
        "prod" -> ".env.prod"
        else -> null
    }
}

private fun parseProfileFromArgs(args: Array<String>): String? {
    args.forEachIndexed { index, arg ->
        if (arg.startsWith("--spring.profiles.active=")) {
            return extractPrimaryProfile(arg.substringAfter("="))
        }
        if (arg == "--spring.profiles.active" && index + 1 < args.size) {
            return extractPrimaryProfile(args[index + 1])
        }
    }
    return null
}

private fun extractPrimaryProfile(value: String?): String? {
    return value
        ?.split(",")
        ?.firstOrNull()
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
}
