package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.delivery.Delivery
import com.sc7258.springshopflyway.domain.delivery.DeliveryRepository
import com.sc7258.springshopflyway.domain.delivery.DeliveryStatus
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.domain.order.Order
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.model.CreateDeliveryRequest
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DeliveryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    private lateinit var testOrder: Order

    @BeforeEach
    fun setup() {
        val member = memberRepository.save(
            Member(
                email = "delivery@example.com",
                password = "password",
                name = "Delivery User",
                role = Role.USER
            )
        )
        testOrder = orderRepository.save(Order(member = member))
    }

    @Test
    @WithMockUser(username = "delivery@example.com", roles = ["USER"])
    fun `배송을 생성한다`() {
        val request = CreateDeliveryRequest(
            orderId = testOrder.id!!,
            city = "Seoul",
            street = "Gangnam-gu",
            zipcode = "12345"
        )

        mockMvc.perform(
            post("/api/v1/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    @WithMockUser(username = "delivery@example.com", roles = ["USER"])
    fun `배송 상태를 조회한다`() {
        val delivery = deliveryRepository.save(
            Delivery(
                order = testOrder,
                address = Address("Seoul", "Gangnam", "12345"),
                status = DeliveryStatus.READY
            )
        )

        mockMvc.perform(
            get("/api/v1/deliveries/${delivery.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("READY"))
            .andExpect(jsonPath("$.address.city").value("Seoul"))
    }

    @Test
    @WithMockUser(username = "delivery@example.com", roles = ["USER"])
    fun `존재하지 않는 배송 조회 시 404를 반환한다`() {
        mockMvc.perform(
            get("/api/v1/deliveries/99999")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }
}
