package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.DuplicateEmailException
import com.sc7258.springshopflyway.common.security.JwtTokenProvider
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.LoginResponse
import com.sc7258.springshopflyway.model.SignupRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun signup(signupRequest: SignupRequest): Long {
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

        return memberRepository.save(member).id!!
    }

    @Transactional(readOnly = true)
    fun login(loginRequest: LoginRequest): String {
        val member = memberRepository.findByEmail(loginRequest.email)
            ?: throw IllegalArgumentException("Invalid email or password") // TODO: Custom Exception

        if (!passwordEncoder.matches(loginRequest.password, member.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return jwtTokenProvider.createToken(member.email, member.role.name)
    }
}
