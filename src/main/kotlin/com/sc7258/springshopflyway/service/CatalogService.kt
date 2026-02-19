package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.model.BookListResponse
import com.sc7258.springshopflyway.model.BookResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CatalogService(
    private val bookRepository: BookRepository
) {

    @Transactional(readOnly = true)
    fun getBooks(page: Int, size: Int, keyword: String?): BookListResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        
        val bookPage = if (keyword.isNullOrBlank()) {
            bookRepository.findAll(pageable)
        } else {
            bookRepository.findByTitleContainingIgnoreCase(keyword, pageable)
        }

        val content = bookPage.content.map { book ->
            BookResponse(
                id = book.id,
                title = book.title,
                author = book.author,
                price = book.price,
                stockQuantity = book.stockQuantity
            )
        }

        return BookListResponse(
            content = content,
            totalElements = bookPage.totalElements.toInt(),
            totalPages = bookPage.totalPages
        )
    }

    @Transactional(readOnly = true)
    fun getBookById(bookId: Long): BookResponse {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException("Book not found with id: $bookId") }

        return BookResponse(
            id = book.id,
            title = book.title,
            author = book.author,
            price = book.price,
            stockQuantity = book.stockQuantity
        )
    }
}
