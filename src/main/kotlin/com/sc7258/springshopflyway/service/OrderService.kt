package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.order.Order
import com.sc7258.springshopflyway.domain.order.OrderItem
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.domain.order.OrderStatus
import com.sc7258.springshopflyway.model.CreateOrderRequest
import com.sc7258.springshopflyway.model.OrderResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val bookRepository: BookRepository,
    private val mockPaymentService: MockPaymentService
) {

    @Transactional
    fun createOrder(email: String, request: CreateOrderRequest): Long {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")

        val order = Order(member = member)

        val items = request.orderItems ?: emptyList()
        if (items.isEmpty()) {
            throw IllegalArgumentException("Order items cannot be empty")
        }

        items.forEach { itemRequest ->
            val bookId = itemRequest.bookId ?: throw IllegalArgumentException("Book ID is required")
            val count = itemRequest.count ?: throw IllegalArgumentException("Count is required")

            val book = bookRepository.findById(bookId)
                .orElseThrow { EntityNotFoundException("Book not found: $bookId") }

            val orderItem = OrderItem(
                book = book,
                orderPrice = book.price,
                count = count
            )
            order.addOrderItem(orderItem)
        }

        // 결제 시도
        try {
            mockPaymentService.processPayment(order.totalAmount)
            order.status = OrderStatus.PAID
        } catch (e: Exception) {
            throw IllegalStateException("Payment failed", e)
        }

        return orderRepository.save(order).id!!
    }

    @Transactional(readOnly = true)
    fun getOrders(email: String): List<OrderResponse> {
        val orders = orderRepository.findAllByMemberEmailOrderByOrderedAtDesc(email)
        
        return orders.map { order ->
            OrderResponse(
                id = order.id,
                status = order.status.name,
                totalAmount = order.totalAmount,
                orderedAt = java.time.OffsetDateTime.of(order.orderedAt, java.time.ZoneOffset.UTC)
            )
        }
    }

    @Transactional
    fun cancelOrder(email: String, orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Order not found: $orderId") }

        if (order.member.email != email) {
            throw IllegalArgumentException("Not authorized to cancel this order") // TODO: Custom Exception
        }

        order.cancel()
        
        // 결제 취소 (결제 ID가 없으므로 주문 ID를 대신 사용하거나, 추후 Payment 엔티티 추가 필요)
        mockPaymentService.cancelPayment(orderId.toString())
    }
}
