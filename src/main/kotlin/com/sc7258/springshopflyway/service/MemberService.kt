package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.DuplicateEmailException
import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.common.exception.InvalidInputException
import com.sc7258.springshopflyway.common.security.LoginTokenIssuer
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.MemberResponse
import com.sc7258.springshopflyway.model.MemberProfileResponse
import com.sc7258.springshopflyway.model.SignupRequest
import com.sc7258.springshopflyway.model.UpdateMyProfileRequest
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

    @Transactional(readOnly = true)
    fun getMyProfile(email: String): MemberProfileResponse {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")
        return toMemberProfileResponse(member)
    }

    @Transactional
    fun updateMyProfile(email: String, request: UpdateMyProfileRequest): MemberProfileResponse {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")

        val updatedName = request.name?.trim()
        if (updatedName != null && updatedName.isEmpty()) {
            throw InvalidInputException("Name must not be blank")
        }
        if (!updatedName.isNullOrEmpty()) {
            member.name = updatedName
        }

        val hasAddressUpdate = request.city != null || request.street != null || request.zipcode != null
        if (hasAddressUpdate) {
            val currentAddress = member.address
            member.address = Address(
                city = request.city ?: currentAddress?.city,
                street = request.street ?: currentAddress?.street,
                zipcode = request.zipcode ?: currentAddress?.zipcode
            )
        }

        return toMemberProfileResponse(member)
    }

    private fun toMemberProfileResponse(member: Member): MemberProfileResponse {
        return MemberProfileResponse(
            id = member.id,
            email = member.email,
            name = member.name,
            role = member.role.name,
            city = member.address?.city,
            street = member.address?.street,
            zipcode = member.address?.zipcode
        )
    }
}
