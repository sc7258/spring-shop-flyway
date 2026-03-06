package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CatalogControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setup() {
        // Flyway로 주입된 초기 데이터가 테스트에 영향을 주지 않도록 삭제
        bookRepository.deleteAll()

        bookRepository.save(
            Book(
                title = "Spring Boot in Action",
                author = "Craig Walls",
                price = 30000,
                stockQuantity = 100,
                isbn = "1234567890"
            )
        )
        bookRepository.save(
            Book(
                title = "Kotlin in Action",
                author = "Dmitry Jemerov",
                price = 32000,
                stockQuantity = 50,
                isbn = "1234567891"
            )
        )
        bookRepository.save(
            Book(
                title = "spring data recipes",
                author = "Data Team",
                price = 28000,
                stockQuantity = 75,
                isbn = "1234567892"
            )
        )
    }

    @Test
    fun `도서 목록을 조회한다`() {
        mockMvc.perform(
            get("/api/v1/books")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.content[0].title").value("spring data recipes"))
    }

    @Test
    fun `키워드로 도서를 검색한다`() {
        mockMvc.perform(
            get("/api/v1/books")
                .param("keyword", "Spring")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].title").value("spring data recipes"))
            .andExpect(jsonPath("$.content[1].title").value("Spring Boot in Action"))
    }

    @Test
    fun `도서 목록을 페이지 단위로 조회한다`() {
        mockMvc.perform(
            get("/api/v1/books")
                .param("page", "1")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.content[0].title").value("Kotlin in Action"))
    }

    @Test
    fun `도서 상세 정보를 조회한다`() {
        val savedBook = bookRepository.findAll().first()

        mockMvc.perform(
            get("/api/v1/books/${savedBook.id}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Spring Boot in Action"))
            .andExpect(jsonPath("$.price").value(30000))
    }

    @Test
    fun `존재하지 않는 도서 조회 시 404를 반환한다`() {
        mockMvc.perform(
            get("/api/v1/books/99999")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value("C002"))
    }
}
