package com.unam.fciencias.urbanincidents.user.model

import com.unam.fciencias.urbanincidents.enums.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val MIN_LENGTH_PASSWORD = 6
const val MAX_LENGTH_PASSWORD = 20

const val MIN_LENGTH_NAME = 1
const val MAX_LENGTH_NAME = 30

object ValidationMessages {
    const val USER_ROLE_EMPTY = "Role can't be empty"
    const val PASSWORD_EMPTY = "Password can't be empty"
    const val TOKEN_EMPTY = "Token can't be empty"
    const val EMAIL_EMPTY = "Email cant't be empty"

    const val EMAIL_INVALID = "Invalid email"

    const val PASSWORD_SIZE =
            "Password must be at least $MIN_LENGTH_PASSWORD characters and at most $MAX_LENGTH_PASSWORD characters"
}

data class Token(
        @field:NotBlank(message = ValidationMessages.TOKEN_EMPTY) val token: String,
)

data class UserId(val id: String)

data class LogoutMessage(val message: String)

@Document("users")
data class User(
        @Id val id: String?,
        val email: String,
        val password: String,
        val role: USER_ROLE,
        val token: String,
        val incidents: List<String>?
)

data class CreaterUserRequest(
        @field:NotBlank(message = ValidationMessages.EMAIL_EMPTY)
        @field:Email(message = ValidationMessages.EMAIL_INVALID)
        val email: String,
        @field:NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
        @field:Size(
                min = MIN_LENGTH_PASSWORD,
                max = MAX_LENGTH_PASSWORD,
                message = ValidationMessages.PASSWORD_SIZE
        )
        val password: String
)

data class LoginRequest(
        @field:NotBlank(message = ValidationMessages.EMAIL_EMPTY)
        @field:Email(message = ValidationMessages.EMAIL_INVALID)
        val email: String,
        @field:NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
        @field:Size(
                min = MIN_LENGTH_PASSWORD,
                max = MAX_LENGTH_PASSWORD,
                message = ValidationMessages.PASSWORD_SIZE
        )
        val password: String
)

data class PatchUserRequest(
        @field:NotBlank(message = ValidationMessages.EMAIL_EMPTY)
        @field:Email(message = ValidationMessages.EMAIL_INVALID)
        val email: String?,
        @field:NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
        @field:Size(
                min = MIN_LENGTH_PASSWORD,
                max = MAX_LENGTH_PASSWORD,
                message = ValidationMessages.PASSWORD_SIZE
        )
        val password: String?,
)
