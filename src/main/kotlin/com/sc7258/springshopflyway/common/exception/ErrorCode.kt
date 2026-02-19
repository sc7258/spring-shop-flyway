package com.sc7258.springshopflyway.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val status: HttpStatus
) {
    // Common
    INVALID_INPUT_VALUE("C001", "Invalid Input Value", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND("C002", "Entity Not Found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("C003", "Payment Failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Member
    EMAIL_ALREADY_EXISTS("M001", "Email Already Exists", HttpStatus.CONFLICT),
    LOGIN_FAILED("M002", "Login Failed", HttpStatus.UNAUTHORIZED),

    // Catalog
    OUT_OF_STOCK("B001", "Out of Stock", HttpStatus.CONFLICT),

    // Order
    ORDER_NOT_FOUND("O001", "Order Not Found", HttpStatus.NOT_FOUND),

    // Delivery
    DELIVERY_NOT_FOUND("D001", "Delivery Not Found", HttpStatus.NOT_FOUND);
}
