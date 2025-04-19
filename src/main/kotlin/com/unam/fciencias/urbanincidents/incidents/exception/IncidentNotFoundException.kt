package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class IncidentNotFoundException(
  message: String = "The incident was not found", errorCode : HttpStatus = HttpStatus.NOT_FOUND): UrbanIncidentsException(message, errorCode) {
}
