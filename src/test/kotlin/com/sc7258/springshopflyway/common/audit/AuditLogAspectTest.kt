package com.sc7258.springshopflyway.common.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.sc7258.springshopflyway.domain.admin.AdminAuditLog
import com.sc7258.springshopflyway.domain.admin.AdminAuditLogRepository
import com.sc7258.springshopflyway.domain.member.Member
import com.sc7258.springshopflyway.domain.member.MemberRepository
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.lang.reflect.Method
import kotlin.test.assertEquals

class AuditLogAspectTest {

    private lateinit var memberRepository: MemberRepository
    private lateinit var adminAuditLogRepository: AdminAuditLogRepository
    private lateinit var objectMapper: ObjectMapper
    private lateinit var auditLogAspect: AuditLogAspect
    private lateinit var joinPoint: ProceedingJoinPoint
    private lateinit var methodSignature: MethodSignature
    private lateinit var auditLogAnnotation: AuditLog

    @BeforeEach
    fun setup() {
        memberRepository = mock(MemberRepository::class.java)
        adminAuditLogRepository = mock(AdminAuditLogRepository::class.java)
        objectMapper = ObjectMapper()
        auditLogAspect = AuditLogAspect(memberRepository, adminAuditLogRepository, objectMapper)
        joinPoint = mock(ProceedingJoinPoint::class.java)
        methodSignature = mock(MethodSignature::class.java)
        auditLogAnnotation = mock(AuditLog::class.java)

        `when`(joinPoint.signature).thenReturn(methodSignature)
    }

    @Test
    fun `should log audit when admin exists`() {
        // Arrange
        val email = "admin@example.com"
        val member = Member(email = email, password = "pw", name = "Admin", id = 1L)
        
        val securityContext = mock(SecurityContext::class.java)
        val authentication = mock(Authentication::class.java)
        `when`(securityContext.authentication).thenReturn(authentication)
        `when`(authentication.name).thenReturn(email)
        SecurityContextHolder.setContext(securityContext)

        `when`(memberRepository.findByEmail(email)).thenReturn(member)
        `when`(auditLogAnnotation.action).thenReturn("TEST_ACTION")
        `when`(auditLogAnnotation.targetIdArgName).thenReturn("targetId")
        
        `when`(methodSignature.parameterNames).thenReturn(arrayOf("targetId", "otherArg"))
        `when`(joinPoint.args).thenReturn(arrayOf("123", "value"))
        
        val method = this::class.java.methods[0] // Just a dummy method
        `when`(methodSignature.method).thenReturn(method)

        // Act
        auditLogAspect.logAudit(joinPoint, auditLogAnnotation)

        // Assert
        verify(joinPoint).proceed()
        verify(adminAuditLogRepository).save(argThat { log ->
            log.adminId == 1L &&
            log.action == "TEST_ACTION" &&
            log.targetId == "123" &&
            log.details!!.contains("value")
        })
    }
}
