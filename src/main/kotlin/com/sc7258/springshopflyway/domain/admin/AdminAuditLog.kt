package com.sc7258.springshopflyway.domain.admin

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "admin_audit_logs")
class AdminAuditLog(
    @Column(name = "admin_id", nullable = false)
    val adminId: Long, // FK to Member.id

    @Column(nullable = false)
    val action: String,

    @Column(name = "target_id")
    val targetId: String? = null,

    @Column(columnDefinition = "TEXT")
    val details: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
