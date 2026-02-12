package com.sc7258.springshopflyway.domain.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByMemberEmailOrderByOrderedAtDesc(email: String): List<Order>
}
