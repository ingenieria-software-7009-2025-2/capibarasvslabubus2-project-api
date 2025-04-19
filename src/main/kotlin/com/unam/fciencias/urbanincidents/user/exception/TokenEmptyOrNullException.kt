package com.unam.fciencias.urbanincidents.exception
import org.springframework.http.HttpStatus

class TokenEmptyOrNullException(
    message: String = "The token is empty or null", 
    errorCode: HttpStatus = HttpStatus.BAD_REQUEST
) : UrbanIncidentsException(message, errorCode) {
}