package com.sc7258.springshopflyway.common.exception

import com.sc7258.springshopflyway.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmailException(e: DuplicateEmailException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = "M001",
            message = e.message ?: "Email already exists",
            status = HttpStatus.CONFLICT.value()
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    // TODO: Add more exception handlers (Validation, etc.)
}

class DuplicateEmailException(message: String) : RuntimeException(message)
