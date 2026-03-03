package com.sc7258.springshopflyway.config

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.time.Instant

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "spring.datasource.url=jdbc:h2:mem:profile-qa;MODE=MariaDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "keycloak.auth-server-url=http://localhost:9090"
    ]
)
@ActiveProfiles("qa")
@Import(ProfileConfigurationTestSupport::class)
class QaProfileConfigurationTest {

    @Test
    fun `qa 프로필 컨텍스트가 기동된다`() {
    }
}

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "spring.datasource.url=jdbc:h2:mem:profile-prod;MODE=MariaDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "keycloak.auth-server-url=http://localhost:9090"
    ]
)
@ActiveProfiles("prod")
@Import(ProfileConfigurationTestSupport::class)
class ProdProfileConfigurationTest {

    @Test
    fun `prod 프로필 컨텍스트가 기동된다`() {
    }
}

@TestConfiguration
class ProfileConfigurationTestSupport {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return JwtDecoder { token ->
            val now = Instant.now()
            Jwt(
                token,
                now,
                now.plusSeconds(3600),
                mapOf("alg" to "none"),
                mapOf("sub" to "profile-user", "preferred_username" to "profile-user")
            )
        }
    }
}
