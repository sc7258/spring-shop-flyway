package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.BookListResponse
import com.sc7258.springshopflyway.model.BookResponse
import com.sc7258.springshopflyway.service.CatalogService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CatalogApiDelegateImpl(
    private val catalogService: CatalogService
) : CatalogApiDelegate {

    override fun getBooks(page: Int, size: Int, keyword: String?): ResponseEntity<BookListResponse> {
        val response = catalogService.getBooks(page, size, keyword)
        return ResponseEntity.ok(response)
    }

    override fun getBookById(bookId: Long): ResponseEntity<BookResponse> {
        val response = catalogService.getBookById(bookId)
        return ResponseEntity.ok(response)
    }
}
