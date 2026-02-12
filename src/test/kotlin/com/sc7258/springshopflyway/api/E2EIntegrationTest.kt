package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.model.*
import com.sc7258.springshopflyway.service.MockPaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class E2EIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bookRepository: BookRepository

    @MockBean
    private lateinit var mockPaymentService: MockPaymentService

    private var bookId: Long = 0

    @BeforeEach
    fun setup() {
        // Mock Payment Service 설정
        Mockito.`when`(mockPaymentService.processPayment(Mockito.anyInt()))
            .thenReturn(UUID.randomUUID().toString())

        // 테스트용 도서 생성
        val book = bookRepository.save(
            Book(title = "E2E Book", author = "Author", price = 10000, stockQuantity = 100)
        )
        bookId = book.id!!
    }

    @Test
    fun `전체 주문 배송 시나리오 테스트`() {
        // 1. 회원가입
        val signupRequest = SignupRequest(
            email = "e2e@example.com",
            password = "password123",
            name = "E2E User",
            city = "Seoul",
            street = "Gangnam",
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
            email = "e2e@example.com",
            password = "password123"
        )
        val loginResult = mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        val loginResponse = objectMapper.readValue(loginResult.response.contentAsString, LoginResponse::class.java)
        val token = loginResponse.accessToken
        assertNotNull(token)

        // 3. 도서 검색 (생략 가능하지만 시나리오상 포함)
        mockMvc.perform(
            get("/api/v1/books")
                .param("keyword", "E2E")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        // 4. 주문 생성
        val createOrderRequest = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = bookId, count = 2)
            )
        )
        val orderResult = mockMvc.perform(
            post("/api/v1/orders")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val orderLocation = orderResult.response.getHeader("Location")
        val orderId = orderLocation!!.substringAfterLast("/").toLong()

        // 5. 배송 생성
        val createDeliveryRequest = CreateDeliveryRequest(
            orderId = orderId,
            city = "Seoul",
            street = "Gangnam",
            zipcode = "12345"
        )
        val deliveryResult = mockMvc.perform(
            post("/api/v1/deliveries")
                .header("Authorization", "Bearer $token") // 배송 생성도 인증 필요
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDeliveryRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val deliveryLocation = deliveryResult.response.getHeader("Location")
        val deliveryId = deliveryLocation!!.substringAfterLast("/").toLong()

        // 6. 배송 조회
        val deliveryCheckResult = mockMvc.perform(
            get("/api/v1/deliveries/$deliveryId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val deliveryResponse = objectMapper.readValue(deliveryCheckResult.response.contentAsString, DeliveryResponse::class.java)
        assertEquals("READY", deliveryResponse.status!!.name)
        assertEquals(orderId, deliveryResponse.orderId)
    }
}
