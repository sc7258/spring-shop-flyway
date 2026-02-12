package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.DeliveryNotFoundException
import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.domain.delivery.Delivery
import com.sc7258.springshopflyway.domain.delivery.DeliveryRepository
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.model.CreateDeliveryRequest
import com.sc7258.springshopflyway.model.DeliveryResponse
import com.sc7258.springshopflyway.model.DeliveryResponseAddress
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeliveryService(
    private val deliveryRepository: DeliveryRepository,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun createDelivery(request: CreateDeliveryRequest): Long {
        val order = orderRepository.findById(request.orderId)
            .orElseThrow { EntityNotFoundException("Order not found: ${request.orderId}") }

        val address = Address(
            city = request.city,
            street = request.street,
            zipcode = request.zipcode
        )

        val delivery = Delivery(
            order = order,
            address = address
        )

        return deliveryRepository.save(delivery).id!!
    }

    @Transactional(readOnly = true)
    fun getDeliveryById(deliveryId: Long): DeliveryResponse {
        val delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow { DeliveryNotFoundException("Delivery not found: $deliveryId") }

        return DeliveryResponse(
            id = delivery.id,
            orderId = delivery.order.id,
            status = DeliveryResponse.Status.valueOf(delivery.status.name),
            trackingNumber = delivery.trackingNumber,
            address = DeliveryResponseAddress(
                city = delivery.address.city,
                street = delivery.address.street,
                zipcode = delivery.address.zipcode
            )
        )
    }
}
