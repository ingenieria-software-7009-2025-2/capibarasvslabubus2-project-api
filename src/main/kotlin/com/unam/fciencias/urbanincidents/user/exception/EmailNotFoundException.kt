package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class EmailNotFoundException(
  message: String = "The incident was not found", errorCode : HttpStatus = HttpStatus.NOT_FOUND): UrbanIncidentsException(message, errorCode) {
        companion object {
        fun generateMessageWithEmail(email: String): String {
            return "There is no user associated with the email $email"
        }
    }
}
