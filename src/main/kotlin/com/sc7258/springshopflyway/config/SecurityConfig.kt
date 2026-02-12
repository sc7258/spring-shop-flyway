package com.sc7258.springshopflyway.config

import com.sc7258.springshopflyway.common.security.JwtAuthenticationFilter
import com.sc7258.springshopflyway.common.security.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                AntPathRequestMatcher("/h2-console/**"),
                AntPathRequestMatcher("/favicon.ico"),
                AntPathRequestMatcher("/error"),
                AntPathRequestMatcher("/swagger-ui/**"),
                AntPathRequestMatcher("/swagger-ui.html"),
                AntPathRequestMatcher("/swagger-resources/**"),
                AntPathRequestMatcher("/v3/api-docs/**"),
                AntPathRequestMatcher("/api/v1/swagger-ui/**"),
                AntPathRequestMatcher("/api/v1/swagger-ui.html"),
                AntPathRequestMatcher("/api/v1/openapi.yaml") // 경로 변경
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
                .anyRequest().authenticated()
            }
            .headers {
                it.frameOptions { frameOptions -> frameOptions.disable() }
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
