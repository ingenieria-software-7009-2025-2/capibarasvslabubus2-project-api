package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

open class UrbanIncidentsException(
    message: String = "An error occurred in the urban incidents system",
    val errorCode: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)


data class ErrorResponse(
    val error: String,           
    val exceptionType: String, 
)

