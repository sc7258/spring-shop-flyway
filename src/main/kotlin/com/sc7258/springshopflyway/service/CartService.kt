package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.common.exception.InvalidInputException
import com.sc7258.springshopflyway.domain.cart.CartItem
import com.sc7258.springshopflyway.domain.cart.CartItemRepository
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.model.AddCartItemRequest
import com.sc7258.springshopflyway.model.CartItemResponse
import com.sc7258.springshopflyway.model.CartResponse
import com.sc7258.springshopflyway.model.UpdateCartItemRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartItemRepository: CartItemRepository,
    private val memberRepository: MemberRepository,
    private val bookRepository: BookRepository
) {
    @Transactional
    fun addCartItem(email: String, request: AddCartItemRequest) {
        validateQuantity(request.quantity)

        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")
        val book = bookRepository.findById(request.bookId)
            .orElseThrow { EntityNotFoundException("Book not found: ${request.bookId}") }

        val existingItem = cartItemRepository.findByMemberEmailAndBookId(email, request.bookId)
        if (existingItem != null) {
            existingItem.increaseQuantity(request.quantity)
            return
        }

        cartItemRepository.save(
            CartItem(
                member = member,
                book = book,
                quantity = request.quantity
            )
        )
    }

    @Transactional(readOnly = true)
    fun getCart(email: String): CartResponse {
        val items = cartItemRepository.findAllByMemberEmailOrderByCreatedAtAsc(email)
        return toCartResponse(items)
    }

    @Transactional
    fun updateCartItem(email: String, bookId: Long, request: UpdateCartItemRequest): CartResponse {
        validateQuantity(request.quantity)

        val cartItem = cartItemRepository.findByMemberEmailAndBookId(email, bookId)
            ?: throw EntityNotFoundException("Cart item not found for book: $bookId")

        cartItem.changeQuantity(request.quantity)
        return toCartResponse(cartItemRepository.findAllByMemberEmailOrderByCreatedAtAsc(email))
    }

    @Transactional
    fun deleteCartItem(email: String, bookId: Long) {
        val cartItem = cartItemRepository.findByMemberEmailAndBookId(email, bookId)
            ?: throw EntityNotFoundException("Cart item not found for book: $bookId")
        cartItemRepository.delete(cartItem)
    }

    private fun validateQuantity(quantity: Int) {
        if (quantity < 1) {
            throw InvalidInputException("Quantity must be greater than zero")
        }
    }

    private fun toCartResponse(items: List<CartItem>): CartResponse {
        val responses = items.map { item ->
            val subtotal = item.book.price * item.quantity
            CartItemResponse(
                bookId = item.book.id,
                title = item.book.title,
                author = item.book.author,
                unitPrice = item.book.price,
                quantity = item.quantity,
                subtotal = subtotal
            )
        }

        return CartResponse(
            items = responses,
            totalQuantity = items.sumOf { it.quantity },
            totalAmount = items.sumOf { it.book.price * it.quantity }
        )
    }
}
