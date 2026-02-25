package com.sc7258.springshopflyway.domain.admin

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminAuditLogRepository : JpaRepository<AdminAuditLog, Long>
