package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.cart.CartItem
import com.sc7258.springshopflyway.domain.cart.CartItemRepository
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.model.AddCartItemRequest
import com.sc7258.springshopflyway.model.UpdateCartItemRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CartControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    private lateinit var member: Member
    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        member = memberRepository.save(
            Member(
                email = "cart@example.com",
                password = "password",
                name = "Cart User",
                role = Role.USER
            )
        )
        book = bookRepository.save(
            Book(
                title = "Cart Book",
                author = "Cart Author",
                price = 15000,
                stockQuantity = 20,
                isbn = "cart-isbn-001"
            )
        )
    }

    @Test
    @WithMockUser(username = "cart@example.com", roles = ["USER"])
    fun `장바구니에 상품을 담는다`() {
        val request = AddCartItemRequest(bookId = book.id!!, quantity = 2)

        mockMvc.perform(
            post("/api/v1/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/cart"))
    }

    @Test
    @WithMockUser(username = "cart@example.com", roles = ["USER"])
    fun `장바구니를 조회한다`() {
        cartItemRepository.save(CartItem(member = member, book = book, quantity = 2))

        mockMvc.perform(
            get("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].bookId").value(book.id!!.toInt()))
            .andExpect(jsonPath("$.items[0].quantity").value(2))
            .andExpect(jsonPath("$.totalQuantity").value(2))
            .andExpect(jsonPath("$.totalAmount").value(30000))
    }

    @Test
    @WithMockUser(username = "cart@example.com", roles = ["USER"])
    fun `장바구니 상품 수량을 수정한다`() {
        cartItemRepository.save(CartItem(member = member, book = book, quantity = 1))
        val request = UpdateCartItemRequest(quantity = 3)

        mockMvc.perform(
            put("/api/v1/cart/items/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].quantity").value(3))
            .andExpect(jsonPath("$.totalAmount").value(45000))
    }

    @Test
    @WithMockUser(username = "cart@example.com", roles = ["USER"])
    fun `장바구니 상품을 삭제한다`() {
        cartItemRepository.save(CartItem(member = member, book = book, quantity = 1))

        mockMvc.perform(
            delete("/api/v1/cart/items/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `인증되지 않은 사용자는 장바구니를 조회할 수 없다`() {
        mockMvc.perform(
            get("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }
}
