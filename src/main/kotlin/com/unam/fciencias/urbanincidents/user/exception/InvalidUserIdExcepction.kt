package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidUserIdException(
        message: String = "The given id is invalid",
        errorCode: HttpStatus = HttpStatus.NOT_FOUND
) : UrbanIncidentsException(message, errorCode) {}
