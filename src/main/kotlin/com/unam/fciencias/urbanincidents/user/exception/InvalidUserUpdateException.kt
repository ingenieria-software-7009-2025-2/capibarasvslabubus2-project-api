package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidUserUpdateException(
        message: String = "Invalid values for an update",
        errorCode: HttpStatus = HttpStatus.BAD_REQUEST
) : UrbanIncidentsException(message, errorCode) {}
