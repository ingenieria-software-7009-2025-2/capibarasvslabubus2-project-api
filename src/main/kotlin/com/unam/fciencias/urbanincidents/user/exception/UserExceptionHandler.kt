package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

import org.slf4j.Logger
import org.slf4j.LoggerFactory; 

@RestControllerAdvice(basePackages = ["com.unam.fciencias.urbanincidents.user"])
class UserExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ResponseEntity<Map<String, String>> {

        // logger.warn("This user already exists: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }

    @ExceptionHandler(TokenEmptyOrNullException::class)
    fun handleNullOrEmptyToken(ex: TokenEmptyOrNullException): ResponseEntity<Map<String, String>> {

        // logger.warn("This user already exists: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ResponseEntity<Map<String, String>> {

        // logger.warn("This user already exists: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleEmailNotFound(ex: EmailNotFoundException): ResponseEntity<Map<String, String>> {

        // logger.warn("This user already exists: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }    
    
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Map<String, String>> {

        // logger.warn("This user already exists: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message!!))
    }
}
