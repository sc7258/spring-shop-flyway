package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.SignupRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `회원가입 후 로그인하여 토큰을 발급받는다`() {
        // 1. 회원가입
        val signupRequest = SignupRequest(
            email = "integration@example.com",
            password = "password123",
            name = "Integration User",
            city = "Seoul",
            street = "Gangnam-gu",
            zipcode = "12345"
        )

        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
            .andExpect(status().isCreated)

        // 2. 로그인
        val loginRequest = LoginRequest(
            email = "integration@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `잘못된 비밀번호로 로그인 시 실패한다`() {
        // 1. 회원가입
        val signupRequest = SignupRequest(
            email = "fail@example.com",
            password = "password123",
            name = "Fail User"
        )

        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
            .andExpect(status().isCreated)

        // 2. 로그인 실패 시도
        val loginRequest = LoginRequest(
            email = "fail@example.com",
            password = "wrong_password"
        )

        mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }
}
