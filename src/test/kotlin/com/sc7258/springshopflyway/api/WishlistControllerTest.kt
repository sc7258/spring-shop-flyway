package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.member.Role
import com.sc7258.springshopflyway.domain.wishlist.Wishlist
import com.sc7258.springshopflyway.domain.wishlist.WishlistRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WishlistControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var wishlistRepository: WishlistRepository

    private lateinit var member: Member
    private lateinit var book: Book

    @BeforeEach
    fun setup() {
        member = memberRepository.save(
            Member(
                email = "wishlist@example.com",
                password = "password",
                name = "Wishlist User",
                role = Role.USER
            )
        )
        book = bookRepository.save(
            Book(
                title = "Wishlist Book",
                author = "Wishlist Author",
                price = 18000,
                stockQuantity = 7,
                isbn = "wishlist-isbn-001"
            )
        )
    }

    @Test
    @WithMockUser(username = "wishlist@example.com", roles = ["USER"])
    fun `위시리스트에 도서를 추가한다`() {
        mockMvc.perform(
            put("/api/v1/wishlist/books/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(username = "wishlist@example.com", roles = ["USER"])
    fun `위시리스트 목록을 조회한다`() {
        wishlistRepository.save(Wishlist(member = member, book = book))

        mockMvc.perform(
            get("/api/v1/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].bookId").value(book.id!!.toInt()))
            .andExpect(jsonPath("$[0].title").value("Wishlist Book"))
    }

    @Test
    @WithMockUser(username = "wishlist@example.com", roles = ["USER"])
    fun `위시리스트에서 도서를 삭제한다`() {
        wishlistRepository.save(Wishlist(member = member, book = book))

        mockMvc.perform(
            delete("/api/v1/wishlist/books/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(username = "wishlist@example.com", roles = ["USER"])
    fun `같은 도서를 여러 번 추가해도 위시리스트에는 한 번만 저장된다`() {
        mockMvc.perform(
            put("/api/v1/wishlist/books/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            put("/api/v1/wishlist/books/${book.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/v1/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }
}
