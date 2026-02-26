package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.DuplicateEmailException
import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.common.security.LoginTokenIssuer
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.MemberResponse
import com.sc7258.springshopflyway.model.SignupRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val loginTokenIssuer: LoginTokenIssuer
) {

    @Transactional(readOnly = true)
    fun getAllMembers(): List<MemberResponse> {
        return memberRepository.findAll().map { member ->
            MemberResponse(
                id = member.id,
                email = member.email,
                name = member.name,
                role = member.role.name
            )
        }
    }

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

    @Transactional
    fun deleteMember(memberId: Long) {
        val member = memberRepository.findById(memberId)
            .orElseThrow { EntityNotFoundException("Member not found: $memberId") }
        memberRepository.delete(member)
    }

    @Transactional(readOnly = true)
    fun login(loginRequest: LoginRequest): String {
        return loginTokenIssuer.issue(loginRequest.email, loginRequest.password)
    }
}
