package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.domain.order.Order
import com.sc7258.springshopflyway.domain.order.OrderItem
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.model.CreateOrderRequest
import com.sc7258.springshopflyway.model.CreateOrderRequestOrderItemsInner
import com.sc7258.springshopflyway.service.MockPaymentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @MockitoBean
    private lateinit var mockPaymentService: MockPaymentService

    private var book1Id: Long = 0
    private var book2Id: Long = 0
    private lateinit var testMember: Member

    @BeforeEach
    fun setup() {
        // 테스트용 회원 생성
        testMember = memberRepository.save(
            Member(
                email = "test@example.com",
                password = "password",
                name = "Test User",
                role = Role.USER
            )
        )

        // 테스트용 도서 생성
        val book1 = bookRepository.save(
            Book(title = "Book 1", author = "Author 1", price = 10000, stockQuantity = 10)
        )
        val book2 = bookRepository.save(
            Book(title = "Book 2", author = "Author 2", price = 20000, stockQuantity = 5)
        )
        
        book1Id = book1.id!!
        book2Id = book2.id!!

        // Mock Payment Service 설정
        Mockito.`when`(mockPaymentService.processPayment(Mockito.anyInt()))
            .thenReturn(UUID.randomUUID().toString())
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `주문을 생성한다`() {
        val request = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = book1Id, count = 2),
                CreateOrderRequestOrderItemsInner(bookId = book2Id, count = 1)
            )
        )

        mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `인증되지 않은 사용자는 주문할 수 없다`() {
        val request = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = book1Id, count = 1)
            )
        )

        mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `내 주문 목록을 조회한다`() {
        // 주문 데이터 생성
        val order = Order(member = testMember) // totalAmount 초기값 0 사용
        val book1 = bookRepository.findById(book1Id).get()
        val book2 = bookRepository.findById(book2Id).get()
        
        // addOrderItem 시 totalAmount 자동 계산 (10000 + 20000 = 30000)
        order.addOrderItem(OrderItem(book = book1, orderPrice = 10000, count = 1))
        order.addOrderItem(OrderItem(book = book2, orderPrice = 20000, count = 1))
        orderRepository.save(order)

        mockMvc.perform(
            get("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].totalAmount").value(30000))
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `주문을 취소한다`() {
        // 주문 생성
        val order = Order(member = testMember)
        val book1 = bookRepository.findById(book1Id).get()
        order.addOrderItem(OrderItem(book = book1, orderPrice = 10000, count = 1))
        val savedOrder = orderRepository.save(order)

        mockMvc.perform(
            post("/api/v1/orders/${savedOrder.id}/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `존재하지 않는 주문 취소 시 404를 반환한다`() {
        mockMvc.perform(
            post("/api/v1/orders/99999/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `재고가 부족하면 주문 생성 시 409를 반환한다`() {
        val request = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = book1Id, count = 100)
            )
        )

        mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = ["USER"])
    fun `결제 실패 시 500을 반환한다`() {
        // Mocking payment failure
        Mockito.`when`(mockPaymentService.processPayment(Mockito.anyInt()))
            .thenThrow(RuntimeException("Payment Gateway Error"))

        val request = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = book1Id, count = 1)
            )
        )

        mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isInternalServerError)
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = ["USER"])
    fun `다른 사용자의 주문을 취소할 수 없다`() {
        // 주문 생성 (testMember 소유)
        val order = Order(member = testMember)
        val book1 = bookRepository.findById(book1Id).get()
        order.addOrderItem(OrderItem(book = book1, orderPrice = 10000, count = 1))
        val savedOrder = orderRepository.save(order)

        // other@example.com으로 로그인하여 취소 시도
        mockMvc.perform(
            post("/api/v1/orders/${savedOrder.id}/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }
}
