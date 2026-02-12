package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.domain.order.OrderStatus
import com.sc7258.springshopflyway.model.CreateOrderRequest
import com.sc7258.springshopflyway.model.CreateOrderRequestOrderItemsInner
import com.sc7258.springshopflyway.service.MockPaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderIntegrationTest {

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

    @MockBean
    private lateinit var mockPaymentService: MockPaymentService

    private lateinit var testMember: Member
    private var bookId: Long = 0

    @BeforeEach
    fun setup() {
        // Mock Payment Service 설정
        Mockito.`when`(mockPaymentService.processPayment(Mockito.anyInt()))
            .thenReturn(UUID.randomUUID().toString())

        // 테스트용 회원 생성
        testMember = memberRepository.save(
            Member(
                email = "integration@example.com",
                password = "password",
                name = "Integration User",
                role = Role.USER
            )
        )

        // 테스트용 도서 생성 (재고 10개)
        val book = bookRepository.save(
            Book(title = "Integration Book", author = "Author", price = 10000, stockQuantity = 10)
        )
        bookId = book.id!!
    }

    @Test
    @WithMockUser(username = "integration@example.com", roles = ["USER"])
    fun `주문 프로세스 통합 테스트`() {
        // 1. 주문 생성 (2권 주문)
        val createRequest = CreateOrderRequest(
            orderItems = listOf(
                CreateOrderRequestOrderItemsInner(bookId = bookId, count = 2)
            )
        )

        val result = mockMvc.perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val location = result.response.getHeader("Location")
        val orderId = location!!.substringAfterLast("/").toLong()

        // 2. 주문 확인 및 재고 차감 검증
        val order = orderRepository.findById(orderId).get()
        assertEquals(OrderStatus.PAID, order.status)
        assertEquals(20000, order.totalAmount)

        val bookAfterOrder = bookRepository.findById(bookId).get()
        assertEquals(8, bookAfterOrder.stockQuantity) // 10 - 2 = 8

        // 3. 주문 목록 조회
        mockMvc.perform(
            get("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(orderId))
            .andExpect(jsonPath("$[0].status").value("PAID"))

        // 4. 주문 취소
        mockMvc.perform(
            post("/api/v1/orders/$orderId/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        // 5. 취소 후 상태 및 재고 복구 검증
        // 영속성 컨텍스트 초기화 (DB에서 다시 조회하기 위해)
        // @Transactional 테스트에서는 같은 트랜잭션 내에서 1차 캐시가 유지되므로,
        // 변경된 상태를 확인하려면 엔티티를 다시 조회해야 함.
        // 하지만 여기서는 이미 조회된 엔티티 객체(order)는 예전 상태이므로, 다시 findById로 가져와야 함.
        // 또한, 서비스에서 변경된 내용이 영속성 컨텍스트에 반영되었으므로, 다시 조회하면 변경된 값을 볼 수 있음.
        
        // 주의: 서비스 메서드 호출 시 트랜잭션이 전파되거나 새로 시작되는데,
        // 테스트 메서드 전체가 @Transactional이므로 같은 트랜잭션을 공유함.
        // 따라서 서비스에서 변경한 내용(Dirty Checking)은 flush 되지 않았을 수 있지만,
        // 조회 시점에는 1차 캐시의 내용을 가져오므로 변경된 객체를 가져올 수 있음.
        // 다만, 확실하게 하기 위해 entityManager.refresh(order)를 하거나 다시 조회하는 것이 좋음.
        
        // 여기서는 간단히 다시 조회
        val cancelledOrder = orderRepository.findById(orderId).get()
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.status)

        val bookAfterCancel = bookRepository.findById(bookId).get()
        assertEquals(10, bookAfterCancel.stockQuantity) // 8 + 2 = 10
    }
}
