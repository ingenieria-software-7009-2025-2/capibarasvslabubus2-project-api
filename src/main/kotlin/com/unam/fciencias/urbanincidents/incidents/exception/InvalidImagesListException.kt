package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidImagesListException(
  message: String = "The list of images is invalid"): UrbanIncidentsException(message) {
}
