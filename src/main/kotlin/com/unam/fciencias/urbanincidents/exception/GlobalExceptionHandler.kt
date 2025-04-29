package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
            ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        val firstErrorMessage =
                ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
                        ?: "Error de validación en la solicitud."

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = firstErrorMessage,
                        exceptionType = ex::class.simpleName ?: "MethodArgumentNotValidException"
                )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseErrors(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorMessage =
                if (ex.message?.contains("JSON parse error") == true) {
                    "El formato del JSON es inválido o faltan campos obligatorios."
                } else {
                    "Se produjo un error al procesar el JSON."
                }

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = errorMessage,
                        exceptionType = ex::class.simpleName ?: "HttpMessageNotReadableException"
                )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
