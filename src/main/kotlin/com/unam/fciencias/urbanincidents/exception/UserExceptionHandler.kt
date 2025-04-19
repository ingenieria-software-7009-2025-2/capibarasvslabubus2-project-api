package com.unam.fciencias.urbanincidents.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserExceptionHandler {
    @ExceptionHandler(
        UserAlreadyExistsException::class,
        TokenEmptyOrNullException::class,
        InvalidTokenException::class,
        EmailNotFoundException::class,
        UserNotFoundException::class
    )
    fun handleUserExceptions(ex: UrbanIncidentsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ex.message ?: "Unknown error", 
            exceptionType = ex.javaClass.simpleName 
        )
        return ResponseEntity.status(ex.errorCode)
            .body(errorResponse)
    }
}
