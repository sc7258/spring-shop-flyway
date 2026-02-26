package com.sc7258.springshopflyway.common.security

interface LoginTokenIssuer {
    fun issue(email: String, password: String): String
}
