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
import com.sc7258.springshopflyway.domain.order.OrderStatus
import com.sc7258.springshopflyway.model.CreateBookRequest
import com.sc7258.springshopflyway.model.UpdateBookRequest
import com.sc7258.springshopflyway.service.MockPaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerTest {

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

    private var orderId: Long = 0
    private var deletableMemberId: Long = 0

    @BeforeEach
    fun setup() {
        memberRepository.save(
            Member(
                email = "admin@example.com",
                password = "password",
                name = "Admin User",
                role = Role.ADMIN
            )
        )
        val normalMember = memberRepository.save(
            Member(
                email = "user@example.com",
                password = "password",
                name = "Normal User",
                role = Role.USER
            )
        )
        deletableMemberId = memberRepository.save(
            Member(
                email = "delete-target@example.com",
                password = "password",
                name = "Delete Target",
                role = Role.USER
            )
        ).id!!

        val book = bookRepository.save(
            Book(
                title = "Existing Book",
                author = "Author",
                price = 10000,
                stockQuantity = 10,
                isbn = "EXIST-${UUID.randomUUID().toString().substring(0, 8)}",
                category = "IT"
            )
        )

        val order = Order(member = normalMember)
        order.addOrderItem(OrderItem(book = book, orderPrice = 10000, count = 1))
        orderId = orderRepository.save(order).id!!

        Mockito.`when`(mockPaymentService.processPayment(Mockito.anyInt()))
            .thenReturn(UUID.randomUUID().toString())
        Mockito.doNothing().`when`(mockPaymentService).cancelPayment(Mockito.anyString())
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = ["USER"])
    fun `ROLE_USER는 관리자 API 접근 시 403을 반환한다`() {
        mockMvc.perform(
            get("/api/v1/admin/members")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = ["ADMIN"])
    fun `ROLE_ADMIN은 관리자 조회 API 호출이 가능하다`() {
        mockMvc.perform(
            get("/api/v1/admin/members")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)

        mockMvc.perform(
            get("/api/v1/admin/orders")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = ["ADMIN"])
    fun `관리자는 도서를 생성 수정 삭제할 수 있다`() {
        val createRequest = CreateBookRequest(
            title = "Created Book",
            author = "Created Author",
            price = 15000,
            stockQuantity = 3,
            isbn = "NEW-${UUID.randomUUID().toString().substring(0, 8)}",
            category = "NOVEL"
        )

        val createdLocation = mockMvc.perform(
            post("/api/v1/admin/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andReturn()
            .response
            .getHeader("Location")!!

        val createdBookId = createdLocation.substringAfterLast("/").toLong()

        val updateRequest = UpdateBookRequest(
            title = "Updated Book",
            author = "Updated Author",
            price = 18000,
            stockQuantity = 7,
            category = "SCIENCE"
        )

        mockMvc.perform(
            put("/api/v1/admin/books/$createdBookId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Book"))
            .andExpect(jsonPath("$.price").value(18000))

        mockMvc.perform(
            delete("/api/v1/admin/books/$createdBookId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        assertFalse(bookRepository.findById(createdBookId).isPresent)
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = ["ADMIN"])
    fun `관리자는 주문을 강제 취소하고 회원을 삭제할 수 있다`() {
        mockMvc.perform(
            post("/api/v1/admin/orders/$orderId/cancel")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        val cancelledOrder = orderRepository.findById(orderId).orElseThrow()
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.status)

        mockMvc.perform(
            delete("/api/v1/admin/members/$deletableMemberId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        assertFalse(memberRepository.findById(deletableMemberId).isPresent)
    }
}
