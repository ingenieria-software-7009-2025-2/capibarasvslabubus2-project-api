
package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.http.converter.HttpMessageNotReadableException

import org.slf4j.Logger
import org.slf4j.LoggerFactory; 

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java); 

    // Validaciones que fallan (por ejemplo, @NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, String?>> {
        val errors = mutableMapOf<String, String?>()

        ex.bindingResult.fieldErrors.forEach {
            errors[it.field] = it.defaultMessage
        }

        // logger.trace("ERROR con alguna anotacion para verificacion ")

       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    // Cuando se envía un JSON con campos null en tipos no-nullables (String en Kotlin)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseErrors(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {

        // logger.trace("ERROR con campos al queerer transformarlos a JSON")

        val error = mapOf("error" to "Invalid JSON format or missing fields.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
}
