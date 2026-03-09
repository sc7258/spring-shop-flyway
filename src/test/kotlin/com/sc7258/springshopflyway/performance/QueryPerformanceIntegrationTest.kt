package com.sc7258.springshopflyway.performance

import com.sc7258.springshopflyway.domain.cart.CartItem
import com.sc7258.springshopflyway.domain.cart.CartItemRepository
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.review.Review
import com.sc7258.springshopflyway.domain.review.ReviewRepository
import com.sc7258.springshopflyway.domain.wishlist.Wishlist
import com.sc7258.springshopflyway.domain.wishlist.WishlistRepository
import com.sc7258.springshopflyway.service.CartService
import com.sc7258.springshopflyway.service.ReviewService
import com.sc7258.springshopflyway.service.WishlistService
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.SessionFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QueryPerformanceIntegrationTest {

    @Autowired
    private lateinit var cartService: CartService

    @Autowired
    private lateinit var wishlistService: WishlistService

    @Autowired
    private lateinit var reviewService: ReviewService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var wishlistRepository: WishlistRepository

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    private lateinit var sessionFactory: SessionFactory

    @BeforeEach
    fun setup() {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory::class.java)
        sessionFactory.statistics.isStatisticsEnabled = true
    }

    @Test
    fun `장바구니 조회 쿼리 수를 개선한다`() {
        val member = createMember("cart")
        val books = createBooks("cart", 3)
        books.forEachIndexed { index, book ->
            cartItemRepository.save(CartItem(member = member, book = book, quantity = index + 1))
        }

        val baselineQueries = measureQueryCount {
            val items = entityManager.createQuery(
                "select c from CartItem c join c.member m where m.email = :email order by c.createdAt asc",
                CartItem::class.java
            )
                .setParameter("email", member.email)
                .resultList
            items.forEach { it.book.title }
        }

        val optimizedQueries = measureQueryCount {
            cartService.getCart(member.email)
        }
        println("PERF_CART baseline=$baselineQueries optimized=$optimizedQueries")

        assertThat(optimizedQueries).isLessThan(baselineQueries)
        assertThat(optimizedQueries).isLessThanOrEqualTo(2)
    }

    @Test
    fun `위시리스트 조회 쿼리 수를 개선한다`() {
        val member = createMember("wishlist")
        val books = createBooks("wishlist", 3)
        books.forEach { book ->
            wishlistRepository.save(Wishlist(member = member, book = book))
        }

        val baselineQueries = measureQueryCount {
            val items = entityManager.createQuery(
                "select w from Wishlist w join w.member m where m.email = :email order by w.createdAt desc",
                Wishlist::class.java
            )
                .setParameter("email", member.email)
                .resultList
            items.forEach { it.book.title }
        }

        val optimizedQueries = measureQueryCount {
            wishlistService.getWishlist(member.email)
        }
        println("PERF_WISHLIST baseline=$baselineQueries optimized=$optimizedQueries")

        assertThat(optimizedQueries).isLessThan(baselineQueries)
        assertThat(optimizedQueries).isLessThanOrEqualTo(2)
    }

    @Test
    fun `리뷰 조회 쿼리 수를 개선한다`() {
        val book = createBooks("review", 1).first()
        val members = listOf(createMember("reviewA"), createMember("reviewB"), createMember("reviewC"))
        members.forEachIndexed { index, member ->
            reviewRepository.save(
                Review(
                    member = member,
                    book = book,
                    rating = 5 - index,
                    content = "review-$index"
                )
            )
        }

        val baselineQueries = measureQueryCount {
            val reviews = entityManager.createQuery(
                "select r from Review r where r.book.id = :bookId order by r.createdAt desc",
                Review::class.java
            )
                .setParameter("bookId", book.id)
                .resultList
            reviews.forEach { it.member.name }
        }

        val optimizedQueries = measureQueryCount {
            reviewService.getBookReviews(book.id!!)
        }
        println("PERF_REVIEW baseline=$baselineQueries optimized=$optimizedQueries")

        assertThat(optimizedQueries).isLessThan(baselineQueries)
        assertThat(optimizedQueries).isLessThanOrEqualTo(3)
    }

    private fun measureQueryCount(block: () -> Unit): Long {
        entityManager.flush()
        entityManager.clear()
        sessionFactory.statistics.clear()
        block()
        return sessionFactory.statistics.prepareStatementCount
    }

    private fun createMember(prefix: String): Member {
        val token = UUID.randomUUID().toString().substring(0, 8)
        return memberRepository.save(
            Member(
                email = "$prefix-$token@example.com",
                password = "encoded-password",
                name = "$prefix-user"
            )
        )
    }

    private fun createBooks(prefix: String, count: Int): List<Book> {
        return (1..count).map { index ->
            val token = UUID.randomUUID().toString().substring(0, 8)
            bookRepository.save(
                Book(
                    title = "$prefix-book-$index",
                    author = "author-$index",
                    price = 10000 + index,
                    stockQuantity = 100,
                    isbn = "$prefix-$token-$index"
                )
            )
        }
    }
}
