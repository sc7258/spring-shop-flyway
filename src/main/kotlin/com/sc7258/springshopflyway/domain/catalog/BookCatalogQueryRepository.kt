package com.sc7258.springshopflyway.domain.catalog

import com.sc7258.springshopflyway.jooq.generated.tables.references.BOOKS
import com.sc7258.springshopflyway.model.BookListResponse
import com.sc7258.springshopflyway.model.BookResponse
import jakarta.persistence.EntityManager
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class BookCatalogQueryRepository(
    private val dslContext: DSLContext,
    private val entityManager: EntityManager
) {
    fun getBooks(page: Int, size: Int, keyword: String?): BookListResponse {
        // Keep JPA writes visible to jOOQ when both are used in the same transaction.
        entityManager.flush()

        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        val condition = keywordCondition(keyword)

        val totalElements = dslContext
            .selectCount()
            .from(BOOKS)
            .where(condition)
            .fetchOne(0, Int::class.java) ?: 0

        val content = dslContext
            .select(
                BOOKS.ID,
                BOOKS.TITLE,
                BOOKS.AUTHOR,
                BOOKS.PRICE,
                BOOKS.STOCK_QUANTITY
            )
            .from(BOOKS)
            .where(condition)
            .orderBy(BOOKS.ID.desc())
            .limit(pageable.pageSize)
            .offset(pageable.offset.toInt())
            .fetch { record ->
                BookResponse(
                    id = record.get(BOOKS.ID),
                    title = record.get(BOOKS.TITLE),
                    author = record.get(BOOKS.AUTHOR),
                    price = record.get(BOOKS.PRICE),
                    stockQuantity = record.get(BOOKS.STOCK_QUANTITY)
                )
            }

        val totalPages = if (totalElements == 0) 0 else (totalElements + pageable.pageSize - 1) / pageable.pageSize

        return BookListResponse(
            content = content,
            totalElements = totalElements,
            totalPages = totalPages
        )
    }

    private fun keywordCondition(keyword: String?): Condition {
        val normalizedKeyword = keyword?.trim()
        return if (normalizedKeyword.isNullOrEmpty()) {
            DSL.noCondition()
        } else {
            BOOKS.TITLE.containsIgnoreCase(normalizedKeyword)
        }
    }
}
