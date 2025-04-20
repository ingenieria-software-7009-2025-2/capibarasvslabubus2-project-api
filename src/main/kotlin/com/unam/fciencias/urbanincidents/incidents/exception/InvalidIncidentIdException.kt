package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidIncidentIdException(
  message: String = "The given id for the incident is invalid", errorCode : HttpStatus = HttpStatus.BAD_REQUEST): UrbanIncidentsException(message, errorCode) {
}
