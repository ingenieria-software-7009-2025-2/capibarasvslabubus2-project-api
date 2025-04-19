package com.unam.fciencias.urbanincidents.exception
import org.springframework.http.HttpStatus

class UserAlreadyExistsException(
    message: String = "This user is already registerd", 
    errorCode: HttpStatus = HttpStatus.CONFLICT
) : UrbanIncidentsException(message, errorCode) {
    companion object {
        fun generateMessageWithEmail(email: String): String {
            return "User with email $email already exists"
        }
    }
}