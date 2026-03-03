package com.sc7258.springshopflyway.config

import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaAuditingIntegrationTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun `신규 엔티티 저장 시 createdAt 과 updatedAt 이 자동 설정된다`() {
        val saved = bookRepository.saveAndFlush(
            Book(
                title = "Audit Book",
                author = "Audit Author",
                price = 12000,
                stockQuantity = 5,
                isbn = "AUDIT-BOOK-001",
                category = "IT"
            )
        )

        assertNotNull(saved.createdAt)
        assertNotNull(saved.updatedAt)
    }

    @Test
    fun `엔티티 수정 시 updatedAt 이 자동 갱신된다`() {
        val saved = bookRepository.saveAndFlush(
            Book(
                title = "Audit Book 2",
                author = "Audit Author",
                price = 13000,
                stockQuantity = 10,
                isbn = "AUDIT-BOOK-002",
                category = "IT"
            )
        )

        entityManager.clear()

        val persisted = bookRepository.findById(saved.id!!).orElseThrow()
        val originalCreatedAt = persisted.createdAt
        val originalUpdatedAt = persisted.updatedAt

        Thread.sleep(20)

        val book = bookRepository.findById(saved.id!!).orElseThrow()
        book.removeStock(1)
        bookRepository.flush()

        entityManager.clear()

        val updated = bookRepository.findById(saved.id!!).orElseThrow()
        assertEquals(originalCreatedAt, updated.createdAt)
        assertTrue(updated.updatedAt.isAfter(originalUpdatedAt))
    }
}
