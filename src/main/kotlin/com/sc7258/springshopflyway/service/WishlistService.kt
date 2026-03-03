package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.wishlist.Wishlist
import com.sc7258.springshopflyway.domain.wishlist.WishlistRepository
import com.sc7258.springshopflyway.model.WishlistItemResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val memberRepository: MemberRepository,
    private val bookRepository: BookRepository
) {
    @Transactional
    fun addWishlistBook(email: String, bookId: Long) {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException("Book not found: $bookId") }

        if (wishlistRepository.existsByMemberEmailAndBookId(email, bookId)) {
            return
        }

        wishlistRepository.save(Wishlist(member = member, book = book))
    }

    @Transactional(readOnly = true)
    fun getWishlist(email: String): List<WishlistItemResponse> {
        return wishlistRepository.findAllByMemberEmailOrderByCreatedAtDesc(email).map { wishlist ->
            WishlistItemResponse(
                bookId = wishlist.book.id,
                title = wishlist.book.title,
                author = wishlist.book.author,
                price = wishlist.book.price
            )
        }
    }

    @Transactional
    fun deleteWishlistBook(email: String, bookId: Long) {
        wishlistRepository.deleteByMemberEmailAndBookId(email, bookId)
    }
}
