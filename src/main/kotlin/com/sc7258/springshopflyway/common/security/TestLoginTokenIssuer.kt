package com.sc7258.springshopflyway.common.security

import com.sc7258.springshopflyway.common.exception.LoginFailedException
import com.sc7258.springshopflyway.domain.member.MemberRepository
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Base64

@Component
@Profile("test")
class TestLoginTokenIssuer(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) : LoginTokenIssuer {

    override fun issue(email: String, password: String): String {
        val member = memberRepository.findByEmail(email) ?: throw LoginFailedException()
        if (!passwordEncoder.matches(password, member.password)) {
            throw LoginFailedException()
        }

        val encoder = Base64.getUrlEncoder().withoutPadding()
        val header = encoder.encodeToString("""{"alg":"none","typ":"JWT"}""".toByteArray(StandardCharsets.UTF_8))
        val payload = encoder.encodeToString(
            """{"sub":"$email","preferred_username":"$email","email":"$email","scope":"read"}"""
                .toByteArray(StandardCharsets.UTF_8)
        )
        return "$header.$payload."
    }
}
