package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookCatalogQueryRepository
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.model.BookListResponse
import com.sc7258.springshopflyway.model.BookResponse
import com.sc7258.springshopflyway.model.CreateBookRequest
import com.sc7258.springshopflyway.model.UpdateBookRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CatalogService(
    private val bookRepository: BookRepository,
    private val bookCatalogQueryRepository: BookCatalogQueryRepository
) {
    @Transactional
    fun createBook(request: CreateBookRequest): Long {
        val book = Book(
            title = request.title,
            author = request.author,
            price = request.price,
            stockQuantity = request.stockQuantity,
            isbn = request.isbn,
            category = request.category
        )
        return bookRepository.save(book).id!!
    }

    @Transactional
    fun updateBook(bookId: Long, request: UpdateBookRequest): BookResponse {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException("Book not found with id: $bookId") }

        book.update(
            title = request.title,
            author = request.author,
            price = request.price,
            stockQuantity = request.stockQuantity,
            category = request.category
        )

        return BookResponse(
            id = book.id,
            title = book.title,
            author = book.author,
            price = book.price,
            stockQuantity = book.stockQuantity
        )
    }

    @Transactional
    fun deleteBook(bookId: Long) {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException("Book not found with id: $bookId") }
        bookRepository.delete(book)
    }

    @Transactional(readOnly = true)
    fun getBooks(page: Int, size: Int, keyword: String?): BookListResponse {
        return bookCatalogQueryRepository.getBooks(page, size, keyword)
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
