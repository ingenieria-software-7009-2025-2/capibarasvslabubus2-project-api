package com.unam.fciencias.urbanincidents.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    // Validaciones que fallan (por ejemplo, @NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
            ex: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, String?>> {
        val errors = mutableMapOf<String, String?>()
        ex.bindingResult.fieldErrors.forEach { errors[it.field] = it.defaultMessage }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseErrors(
            ex: HttpMessageNotReadableException
    ): ResponseEntity<Map<String, String>> {
        val errorMessage =
                if (ex.message?.contains("JSON parse error") == true) {
                    "El formato del JSON es inválido o faltan campos obligatorios."
                } else {
                    "Se produjo un error al procesar el JSON."
                }
        val errorResponse = mapOf("error" to errorMessage, "message" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
