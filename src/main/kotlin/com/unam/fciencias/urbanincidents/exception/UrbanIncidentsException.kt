package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

open class UrbanIncidentsException(
        message: String = "An error occurred in the urban incidents system",
        val errorCode: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : RuntimeException(message)

data class ErrorResponse(
        val status: Int,
        val error: String,
        val exceptionType: String,
)
