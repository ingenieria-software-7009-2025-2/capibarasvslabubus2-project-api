package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class IncidentExceptionHandler {
    @ExceptionHandler(
            InvalidImagesListException::class,
            IncidentNotFoundException::class,
            InvalidIncidentIdException::class,
            InvalidIncidentDateException::class,
            InvalidIncidentStateException::class,
            InvalidIncidentUpdateException::class,
            UnauthorizedIncidentException::class,
    )
    fun handleIncidentExceptions(ex: UrbanIncidentsException): ResponseEntity<ErrorResponse> {
        val errorResponse =
                ErrorResponse(
                        error = ex.message ?: "Unknown error",
                        exceptionType = ex.javaClass.simpleName,
                        status = ex.errorCode.value(),
                )
        return ResponseEntity.status(ex.errorCode).body(errorResponse)
    }
}
