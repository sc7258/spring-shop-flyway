package com.sc7258.springshopflyway.domain.member

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class Member(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

    @Embedded
    val address: Address? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class Role {
    USER, ADMIN
}

@Embeddable
data class Address(
    @Column(name = "address_city")
    val city: String?,

    @Column(name = "address_street")
    val street: String?,

    @Column(name = "address_zipcode")
    val zipcode: String?
)
