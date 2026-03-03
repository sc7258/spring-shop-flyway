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
import com.sc7258.springshopflyway.domain.review.Review
import com.sc7258.springshopflyway.domain.review.ReviewRepository
import com.sc7258.springshopflyway.model.CreateReviewRequest
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
class ReviewControllerTest {

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

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    private lateinit var purchaser: Member
    private lateinit var nonPurchaser: Member
    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        purchaser = memberRepository.save(
            Member(
                email = "buyer@example.com",
                password = "password",
                name = "Buyer",
                role = Role.USER
            )
        )
        nonPurchaser = memberRepository.save(
            Member(
                email = "visitor@example.com",
                password = "password",
                name = "Visitor",
                role = Role.USER
            )
        )
        book = bookRepository.save(
            Book(
                title = "Review Book",
                author = "Review Author",
                price = 22000,
                stockQuantity = 15,
                isbn = "review-isbn-001"
            )
        )

        val order = Order(member = purchaser)
        order.addOrderItem(OrderItem(book = book, orderPrice = book.price, count = 1))
        orderRepository.save(order)
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = ["USER"])
    fun `구매한 사용자는 리뷰를 작성할 수 있다`() {
        val request = CreateReviewRequest(rating = 5, content = "Excellent book")

        mockMvc.perform(
            post("/api/v1/books/${book.id}/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    @WithMockUser(username = "visitor@example.com", roles = ["USER"])
    fun `구매하지 않은 사용자는 리뷰를 작성할 수 없다`() {
        val request = CreateReviewRequest(rating = 4, content = "Looks nice")

        mockMvc.perform(
            post("/api/v1/books/${book.id}/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.code").value("R001"))
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = ["USER"])
    fun `이미 작성한 리뷰가 있으면 중복 작성할 수 없다`() {
        reviewRepository.save(
            Review(
                member = purchaser,
                book = book,
                rating = 5,
                content = "Already reviewed"
            )
        )
        val request = CreateReviewRequest(rating = 4, content = "Duplicate")

        mockMvc.perform(
            post("/api/v1/books/${book.id}/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value("R002"))
    }

    @Test
    fun `도서별 리뷰 목록은 공개 조회할 수 있다`() {
        reviewRepository.save(
            Review(
                member = purchaser,
                book = book,
                rating = 5,
                content = "Public review"
            )
        )

        mockMvc.perform(
            get("/api/v1/books/${book.id}/reviews")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].bookId").value(book.id!!.toInt()))
            .andExpect(jsonPath("$[0].memberName").value("Buyer"))
            .andExpect(jsonPath("$[0].rating").value(5))
    }
}
