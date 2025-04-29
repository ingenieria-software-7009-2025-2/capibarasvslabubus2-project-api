package com.unam.fciencias.urbanincidents.exception

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
            UserNotFoundException::class,
            InvalidUserIdException::class,
            UnauthorizedUserException::class,
            InvalidUserUpdateException::class,
    )
    fun handleUserExceptions(ex: UrbanIncidentsException): ResponseEntity<ErrorResponse> {
        val errorResponse =
                ErrorResponse(
                        error = ex.message ?: "Unknown error",
                        exceptionType = ex.javaClass.simpleName,
                        status = ex.errorCode.value(),
                )
        return ResponseEntity.status(ex.errorCode).body(errorResponse)
    }
}
