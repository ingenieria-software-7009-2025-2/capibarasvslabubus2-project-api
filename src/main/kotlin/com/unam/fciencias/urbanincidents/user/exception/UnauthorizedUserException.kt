package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class UnauthorizedUserException(
        message: String = "You are not authorized to do this operation",
        errorCode: HttpStatus = HttpStatus.UNAUTHORIZED
) : UrbanIncidentsException(message, errorCode) {}
