package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.CreateOrderRequest
import com.sc7258.springshopflyway.model.OrderResponse
import com.sc7258.springshopflyway.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.net.URI

@Service
class OrderApiDelegateImpl(
    private val orderService: OrderService
) : OrderApiDelegate {

    override fun createOrder(createOrderRequest: CreateOrderRequest): ResponseEntity<Unit> {
        val email = getCurrentUserEmail()
        val orderId = orderService.createOrder(email, createOrderRequest)
        return ResponseEntity.created(URI.create("/api/v1/orders/$orderId")).build()
    }

    override fun getOrders(): ResponseEntity<List<OrderResponse>> {
        val email = getCurrentUserEmail()
        val orders = orderService.getOrders(email)
        return ResponseEntity.ok(orders)
    }

    override fun cancelOrder(orderId: Long): ResponseEntity<Unit> {
        val email = getCurrentUserEmail()
        orderService.cancelOrder(email, orderId)
        return ResponseEntity.noContent().build()
    }

    private fun getCurrentUserEmail(): String {
        return SecurityContextHolder.getContext().authentication.name
    }
}
