package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidIncidentUpdateException(
        message: String = "The update for the incident was invalid",
        errorCode: HttpStatus = HttpStatus.CONFLICT
) : UrbanIncidentsException(message, errorCode) {}
