package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.CreateDeliveryRequest
import com.sc7258.springshopflyway.model.DeliveryResponse
import com.sc7258.springshopflyway.service.DeliveryService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class DeliveryApiDelegateImpl(
    private val deliveryService: DeliveryService
) : DeliveryApiDelegate {

    override fun createDelivery(createDeliveryRequest: CreateDeliveryRequest): ResponseEntity<Unit> {
        val deliveryId = deliveryService.createDelivery(createDeliveryRequest)
        return ResponseEntity.created(URI.create("/api/v1/deliveries/$deliveryId")).build()
    }

    override fun getDeliveryById(deliveryId: Long): ResponseEntity<DeliveryResponse> {
        val response = deliveryService.getDeliveryById(deliveryId)
        return ResponseEntity.ok(response)
    }
}
