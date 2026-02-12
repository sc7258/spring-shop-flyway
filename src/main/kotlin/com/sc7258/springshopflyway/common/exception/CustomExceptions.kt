package com.sc7258.springshopflyway.common.exception

class InvalidInputException(message: String = ErrorCode.INVALID_INPUT_VALUE.message) : 
    BusinessException(ErrorCode.INVALID_INPUT_VALUE, message)

class EntityNotFoundException(message: String = ErrorCode.ENTITY_NOT_FOUND.message) : 
    BusinessException(ErrorCode.ENTITY_NOT_FOUND, message)

class DuplicateEmailException(message: String = ErrorCode.EMAIL_ALREADY_EXISTS.message) : 
    BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, message)

class LoginFailedException(message: String = ErrorCode.LOGIN_FAILED.message) : 
    BusinessException(ErrorCode.LOGIN_FAILED, message)

class OutOfStockException(message: String = ErrorCode.OUT_OF_STOCK.message) : 
    BusinessException(ErrorCode.OUT_OF_STOCK, message)

class OrderNotFoundException(message: String = ErrorCode.ORDER_NOT_FOUND.message) : 
    BusinessException(ErrorCode.ORDER_NOT_FOUND, message)

class DeliveryNotFoundException(message: String = ErrorCode.DELIVERY_NOT_FOUND.message) : 
    BusinessException(ErrorCode.DELIVERY_NOT_FOUND, message)
