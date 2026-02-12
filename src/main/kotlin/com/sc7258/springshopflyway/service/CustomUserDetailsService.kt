package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.domain.member.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val member = memberRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        return User.builder()
            .username(member.email)
            .password(member.password)
            .authorities(SimpleGrantedAuthority("ROLE_${member.role.name}"))
            .build()
    }
}
