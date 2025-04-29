package com.unam.fciencias.urbanincidents.exception

import org.springframework.http.HttpStatus

class InvalidTokenException(
        message: String = "The given token is invalid",
        errorCode: HttpStatus = HttpStatus.NOT_FOUND
) : UrbanIncidentsException(message, errorCode) {
    companion object {
        fun generateMessageWithToken(token: String): String {
            return "There is no user associated with the token $token"
        }
    }
}
