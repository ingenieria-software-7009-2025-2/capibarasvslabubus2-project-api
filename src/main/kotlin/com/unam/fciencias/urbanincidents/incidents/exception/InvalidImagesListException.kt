package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidImagesListException(
  message: String = "The list of images is invalid", errorCode : HttpStatus = HttpStatus.BAD_REQUEST): UrbanIncidentsException(message, errorCode) {
}
