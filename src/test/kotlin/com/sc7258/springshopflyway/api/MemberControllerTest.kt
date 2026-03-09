package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.LoginRequest
import com.sc7258.springshopflyway.model.SignupRequest
import com.sc7258.springshopflyway.model.UpdateMyProfileRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // H2 사용
@Transactional // 테스트 후 롤백
class MemberControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `회원가입 성공`() {
        val request = SignupRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            city = "Seoul",
            street = "Gangnam-gu",
            zipcode = "12345"
        )

        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `중복된 이메일로 가입 시 409를 반환한다`() {
        // 1. 첫 번째 가입
        val request1 = SignupRequest(
            email = "duplicate@example.com",
            password = "password123",
            name = "Test User",
            city = "Seoul",
            street = "Gangnam-gu",
            zipcode = "12345"
        )
        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isCreated)

        // 2. 중복 가입 시도
        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `로그인 성공 시 토큰을 반환한다`() {
        // 회원가입
        val signupRequest = SignupRequest(
            email = "login@example.com",
            password = "password123",
            name = "Login User"
        )
        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )

        // 로그인
        val loginRequest = LoginRequest(
            email = "login@example.com",
            password = "password123"
        )
        mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
    }

    @Test
    fun `잘못된 비밀번호로 로그인 시 401을 반환한다`() {
        // 회원가입
        val signupRequest = SignupRequest(
            email = "wrongpw@example.com",
            password = "password123",
            name = "User"
        )
        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )

        // 로그인 시도
        val loginRequest = LoginRequest(
            email = "wrongpw@example.com",
            password = "wrongpassword"
        )
        mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "me@example.com", roles = ["USER"])
    fun `내 정보를 조회한다`() {
        memberRepository.save(
            Member(
                email = "me@example.com",
                password = "password",
                name = "Me User",
                role = Role.USER
            )
        )

        mockMvc.perform(
            get("/api/v1/members/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("me@example.com"))
            .andExpect(jsonPath("$.name").value("Me User"))
            .andExpect(jsonPath("$.role").value("USER"))
    }

    @Test
    @WithMockUser(username = "me-update@example.com", roles = ["USER"])
    fun `내 정보를 수정한다`() {
        memberRepository.save(
            Member(
                email = "me-update@example.com",
                password = "password",
                name = "Before Name",
                role = Role.USER
            )
        )
        val request = UpdateMyProfileRequest(
            name = "After Name",
            city = "Seoul",
            street = "Gangnam-daero",
            zipcode = "06236"
        )

        mockMvc.perform(
            put("/api/v1/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("After Name"))
            .andExpect(jsonPath("$.city").value("Seoul"))
            .andExpect(jsonPath("$.street").value("Gangnam-daero"))
            .andExpect(jsonPath("$.zipcode").value("06236"))
    }

    @Test
    fun `인증되지 않은 사용자는 내 정보를 조회할 수 없다`() {
        mockMvc.perform(
            get("/api/v1/members/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }
}
