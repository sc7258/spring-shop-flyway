package com.sc7258.springshopflyway.domain.catalog

import com.sc7258.springshopflyway.common.exception.OutOfStockException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
class Book(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var author: String,

    @Column(nullable = false)
    var price: Int,

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int,

    @Column(unique = true)
    val isbn: String? = null,

    var category: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(
        title: String?,
        author: String?,
        price: Int?,
        stockQuantity: Int?,
        category: String?
    ) {
        if (title != null) this.title = title
        if (author != null) this.author = author
        if (price != null) this.price = price
        if (stockQuantity != null) this.stockQuantity = stockQuantity
        this.category = category
        this.updatedAt = LocalDateTime.now()
    }

    fun removeStock(quantity: Int) {
        val restStock = this.stockQuantity - quantity
        if (restStock < 0) {
            throw OutOfStockException("Need more stock. Current: $stockQuantity, Requested: $quantity")
        }
        this.stockQuantity = restStock
    }

    fun addStock(quantity: Int) {
        this.stockQuantity += quantity
    }
}
