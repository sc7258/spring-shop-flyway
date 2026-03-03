package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.WishlistItemResponse
import com.sc7258.springshopflyway.service.WishlistService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class WishlistApiDelegateImpl(
    private val wishlistService: WishlistService
) : WishlistApiDelegate {

    override fun addWishlistBook(bookId: Long): ResponseEntity<Unit> {
        wishlistService.addWishlistBook(getCurrentUserEmail(), bookId)
        return ResponseEntity.noContent().build()
    }

    override fun getWishlist(): ResponseEntity<List<WishlistItemResponse>> {
        return ResponseEntity.ok(wishlistService.getWishlist(getCurrentUserEmail()))
    }

    override fun deleteWishlistBook(bookId: Long): ResponseEntity<Unit> {
        wishlistService.deleteWishlistBook(getCurrentUserEmail(), bookId)
        return ResponseEntity.noContent().build()
    }

    private fun getCurrentUserEmail(): String = SecurityContextHolder.getContext().authentication.name
}
