package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidIncidentStateException(
  message: String = "The state of the incident is invalid", errorCode : HttpStatus = HttpStatus.CONFLICT): UrbanIncidentsException(message, errorCode) {
}
