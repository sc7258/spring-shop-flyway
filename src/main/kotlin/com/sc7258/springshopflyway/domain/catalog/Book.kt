package com.sc7258.springshopflyway.domain.catalog

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
class Book(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val author: String,

    @Column(nullable = false)
    val price: Int,

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int,

    @Column(unique = true)
    val isbn: String? = null,

    val category: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun removeStock(quantity: Int) {
        val restStock = this.stockQuantity - quantity
        if (restStock < 0) {
            throw IllegalStateException("need more stock") // TODO: Custom Exception
        }
        this.stockQuantity = restStock
    }

    fun addStock(quantity: Int) {
        this.stockQuantity += quantity
    }
}
