package com.sc7258.springshopflyway.common.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.admin.AdminAuditLog
import com.sc7258.springshopflyway.domain.admin.AdminAuditLogRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class AuditLogAspect(
    private val memberRepository: MemberRepository,
    private val adminAuditLogRepository: AdminAuditLogRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(AuditLogAspect::class.java)

    @Around("@annotation(auditLog)")
    fun logAudit(joinPoint: ProceedingJoinPoint, auditLog: AuditLog): Any? {
        val result = joinPoint.proceed()
        
        try {
            val authentication = SecurityContextHolder.getContext().authentication
            val email = authentication?.name

            if (email != null) {
                val member = memberRepository.findByEmail(email)
                if (member != null) {
                    val signature = joinPoint.signature as MethodSignature
                    val args = joinPoint.args
                    val parameterNames = signature.parameterNames

                    var targetId: String? = null
                    val detailsMap = mutableMapOf<String, Any?>()

                    for (i in parameterNames.indices) {
                        val paramName = parameterNames[i]
                        val argValue = args[i]
                        
                        detailsMap[paramName] = argValue

                        if (auditLog.targetIdArgName.isNotEmpty() && paramName == auditLog.targetIdArgName) {
                            targetId = argValue?.toString()
                        }
                    }

                    val action = if (auditLog.action.isNotEmpty()) auditLog.action else signature.method.name
                    val details = try {
                        objectMapper.writeValueAsString(detailsMap)
                    } catch (e: Exception) {
                        "Failed to serialize arguments: ${e.message}"
                    }

                    val log = AdminAuditLog(
                        adminId = member.id!!,
                        action = action,
                        targetId = targetId,
                        details = details
                    )
                    adminAuditLogRepository.save(log)
                } else {
                    logger.warn("Admin user not found in database for email: $email")
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to save audit log", e)
        }

        return result
    }
}
