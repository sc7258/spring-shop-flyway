package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.LoginResponse
import com.sc7258.springshopflyway.model.SignupRequest
import com.sc7258.springshopflyway.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class MemberApiDelegateImpl(
    private val memberService: MemberService
) : MemberApiDelegate {

    override fun signup(signupRequest: SignupRequest): ResponseEntity<Unit> {
        val memberId = memberService.signup(signupRequest)
        return ResponseEntity.created(URI.create("/api/v1/members/$memberId")).build()
    }

    override fun login(loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val token = memberService.login(loginRequest)
        return ResponseEntity.ok(LoginResponse(accessToken = token))
    }
}
