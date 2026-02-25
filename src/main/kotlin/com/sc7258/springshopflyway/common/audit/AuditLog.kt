package com.sc7258.springshopflyway.common.audit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuditLog(
    val action: String = "",
    val targetIdArgName: String = ""
)
