package com.sc7258.springshopflyway.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CatalogIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun `도서 목록을 조회하고 상세 정보를 확인한다`() {
        // 1. 도서 목록 조회 (V2__insert_sample_books.sql 데이터 활용)
        // 테스트 환경에서는 V2 스크립트가 실행되지 않았을 수 있으므로, 데이터가 없으면 생성
        if (bookRepository.count() == 0L) {
            bookRepository.save(Book(title = "Spring Boot in Action", author = "Craig Walls", price = 30000, stockQuantity = 100))
        }

        mockMvc.perform(
            get("/api/v1/books")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.totalElements").value(bookRepository.count()))
            .andExpect(jsonPath("$.content[0].title").exists())
            .andDo(MockMvcResultHandlers.print())

        // 2. 첫 번째 도서의 ID로 상세 조회
        val bookId = bookRepository.findAll().first().id

        mockMvc.perform(
            get("/api/v1/books/$bookId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.price").exists())
    }
}
