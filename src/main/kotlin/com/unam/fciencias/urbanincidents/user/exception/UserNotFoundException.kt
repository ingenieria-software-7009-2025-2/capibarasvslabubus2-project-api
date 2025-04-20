package com.unam.fciencias.urbanincidents.exception
import org.springframework.http.HttpStatus

class UserNotFoundException(
    message: String = "User not found", 
    errorCode: HttpStatus = HttpStatus.NOT_FOUND
) : UrbanIncidentsException(message, errorCode) {
}