package com.sc7258.springshopflyway.config

import com.nimbusds.jwt.JWTParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain
import java.time.Instant

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("test")
class TestSecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/h2-console/**",
                "/favicon.ico",
                "/error"
            )
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/members/signup",
                    "/api/v1/members/login",
                    "/api/v1/books/**"
                ).permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }

        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return JwtDecoder { token ->
            try {
                // 실제 토큰 파싱 시도
                val jwt = JWTParser.parse(token)
                val claims = jwt.jwtClaimsSet.claims
                val sub = claims["sub"] as? String ?: "user"
                val scope = claims["scope"] as? String ?: "read"
                val preferredUsername = claims["preferred_username"] as? String ?: sub
                val email = claims["email"] as? String ?: sub
                
                // 만료 시간 등은 테스트 편의를 위해 현재 시간 기준으로 재설정하거나 원본 사용
                val now = Instant.now()
                Jwt(
                    token,
                    now,
                    now.plusSeconds(3600),
                    mapOf("alg" to "none"),
                    mapOf(
                        "sub" to sub,
                        "scope" to scope,
                        "preferred_username" to preferredUsername,
                        "email" to email
                    )
                )
            } catch (e: Exception) {
                // 파싱 실패 시 더미 토큰 반환 (기존 로직 유지)
                val now = Instant.now()
                Jwt(
                    token,
                    now,
                    now.plusSeconds(3600),
                    mapOf("alg" to "none"),
                    mapOf(
                        "sub" to "user",
                        "scope" to "read",
                        "preferred_username" to "user",
                        "email" to "user@example.com"
                    )
                )
            }
        }
    }
}
