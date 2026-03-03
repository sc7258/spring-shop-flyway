package com.sc7258.springshopflyway.domain.delivery

import com.sc7258.springshopflyway.common.persistence.BaseTimeEntity
import com.sc7258.springshopflyway.domain.member.Address
import com.sc7258.springshopflyway.domain.order.Order
import jakarta.persistence.*

@Entity
@Table(name = "deliveries")
class Delivery(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    val order: Order,

    @Embedded
    val address: Address,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DeliveryStatus = DeliveryStatus.READY,

    @Column(name = "tracking_number")
    var trackingNumber: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity()

enum class DeliveryStatus {
    READY, COMP, CANCEL
}
