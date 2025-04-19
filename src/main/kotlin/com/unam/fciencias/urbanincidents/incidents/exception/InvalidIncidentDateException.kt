package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidIncidentDateException(
        message: String = "The date of the incident is invalid",
        errorCode: HttpStatus = HttpStatus.CONFLICT
) : UrbanIncidentsException(message, errorCode) {}
