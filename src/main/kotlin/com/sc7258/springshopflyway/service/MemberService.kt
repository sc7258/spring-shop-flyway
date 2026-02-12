package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.api.MemberApiDelegate
import com.sc7258.springshopflyway.common.exception.DuplicateEmailException
import com.sc7258.springshopflyway.common.security.JwtTokenProvider
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.LoginResponse
import com.sc7258.springshopflyway.model.SignupRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : MemberApiDelegate {

    @Transactional
    override fun signup(signupRequest: SignupRequest): ResponseEntity<Unit> {
        if (memberRepository.existsByEmail(signupRequest.email)) {
            throw DuplicateEmailException("Email already exists: ${signupRequest.email}")
        }

        val member = Member(
            email = signupRequest.email,
            password = passwordEncoder.encode(signupRequest.password),
            name = signupRequest.name,
            role = Role.USER,
            address = Address(
                city = signupRequest.city,
                street = signupRequest.street,
                zipcode = signupRequest.zipcode
            )
        )

        val savedMember = memberRepository.save(member)
        
        return ResponseEntity.created(URI.create("/api/v1/members/${savedMember.id}")).build()
    }

    @Transactional(readOnly = true)
    override fun login(loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val member = memberRepository.findByEmail(loginRequest.email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val token = jwtTokenProvider.createToken(member.email, member.role.name)
        return ResponseEntity.ok(LoginResponse(accessToken = token))
    }
}
