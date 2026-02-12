package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.LoginResponse
import com.sc7258.springshopflyway.model.SignupRequest
import com.sc7258.springshopflyway.service.MemberService
import org.springframework.http.HttpStatus
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
        return try {
            val token = memberService.login(loginRequest)
            ResponseEntity.ok(LoginResponse(accessToken = token))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
