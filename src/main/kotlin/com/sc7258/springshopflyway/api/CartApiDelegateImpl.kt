package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.AddCartItemRequest
import com.sc7258.springshopflyway.model.CartResponse
import com.sc7258.springshopflyway.model.UpdateCartItemRequest
import com.sc7258.springshopflyway.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.net.URI

@Service
class CartApiDelegateImpl(
    private val cartService: CartService
) : CartApiDelegate {

    override fun addCartItem(addCartItemRequest: AddCartItemRequest): ResponseEntity<Unit> {
        cartService.addCartItem(getCurrentUserEmail(), addCartItemRequest)
        return ResponseEntity.created(URI.create("/api/v1/cart")).build()
    }

    override fun getCart(): ResponseEntity<CartResponse> {
        return ResponseEntity.ok(cartService.getCart(getCurrentUserEmail()))
    }

    override fun updateCartItem(bookId: Long, updateCartItemRequest: UpdateCartItemRequest): ResponseEntity<CartResponse> {
        return ResponseEntity.ok(cartService.updateCartItem(getCurrentUserEmail(), bookId, updateCartItemRequest))
    }

    override fun deleteCartItem(bookId: Long): ResponseEntity<Unit> {
        cartService.deleteCartItem(getCurrentUserEmail(), bookId)
        return ResponseEntity.noContent().build()
    }

    private fun getCurrentUserEmail(): String = SecurityContextHolder.getContext().authentication.name
}
