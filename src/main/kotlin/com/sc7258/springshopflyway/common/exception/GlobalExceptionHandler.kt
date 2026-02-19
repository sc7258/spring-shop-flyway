package com.sc7258.springshopflyway.common.exception

import com.sc7258.springshopflyway.model.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        log.warn("BusinessException: code={}, message={}", e.errorCode.code, e.message)
        val response = ErrorResponse(
            code = e.errorCode.code,
            message = e.message ?: e.errorCode.message
        )
        return ResponseEntity.status(e.errorCode.status).body(response)
    }

    // JPA EntityNotFoundException 처리 (기존 코드 호환성 유지 또는 BusinessException으로 변환)
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException::class)
    fun handleJpaEntityNotFoundException(e: jakarta.persistence.EntityNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("EntityNotFoundException: {}", e.message)
        val errorCode = ErrorCode.ENTITY_NOT_FOUND
        val response = ErrorResponse(
            code = errorCode.code,
            message = e.message ?: errorCode.message
        )
        return ResponseEntity.status(errorCode.status).body(response)
    }
    
    // 그 외 예외 처리 (Optional)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unexpected Exception: ", e)
        val response = ErrorResponse(
            code = "C999",
            message = "Internal Server Error: ${e.message}"
        )
        return ResponseEntity.status(500).body(response)
    }
}
