package com.sc7258.springshopflyway.domain.member

import com.sc7258.springshopflyway.common.persistence.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "members")
class Member(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

    @Embedded
    var address: Address? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity()

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
