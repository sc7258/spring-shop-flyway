package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.common.exception.InvalidInputException
import com.sc7258.springshopflyway.common.exception.OrderNotFoundException
import com.sc7258.springshopflyway.common.exception.PaymentFailedException
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.order.Order
import com.sc7258.springshopflyway.domain.order.OrderItem
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.domain.order.OrderStatus
import com.sc7258.springshopflyway.model.CreateOrderRequest
import com.sc7258.springshopflyway.model.OrderResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val bookRepository: BookRepository,
    private val mockPaymentService: MockPaymentService
) {

    private val log = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun createOrder(email: String, request: CreateOrderRequest): Long {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")

        val order = Order(member = member)

        val items = request.orderItems ?: emptyList()
        if (items.isEmpty()) {
            throw InvalidInputException("Order items cannot be empty")
        }

        items.forEach { itemRequest ->
            val bookId = itemRequest.bookId ?: throw InvalidInputException("Book ID is required")
            val count = itemRequest.count ?: throw InvalidInputException("Count is required")

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
            log.error("Payment failed for order of member: {}, amount: {}", email, order.totalAmount, e)
            throw PaymentFailedException("Payment failed: ${e.message}")
        }

        val savedOrder = orderRepository.save(order)
        log.info("Order created: id={}, member={}, amount={}", savedOrder.id, email, savedOrder.totalAmount)
        return savedOrder.id!!
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
            .orElseThrow { OrderNotFoundException("Order not found: $orderId") }

        if (order.member.email != email) {
            throw InvalidInputException("Not authorized to cancel this order")
        }

        log.info("Cancelling order: id={}, member={}", orderId, email)
        order.cancel()
        
        // 결제 취소
        mockPaymentService.cancelPayment(orderId.toString())
    }
}
