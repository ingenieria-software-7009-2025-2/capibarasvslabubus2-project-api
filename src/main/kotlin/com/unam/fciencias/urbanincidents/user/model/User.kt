package com.unam.fciencias.urbanincidents.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.*
import jakarta.validation.Valid

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.validator.NullableNotBlank

const val MIN_LENGTH_PASSWORD = 6
const val MAX_LENGTH_PASSWORD = 20

const val MIN_LENGTH_NAME = 1
const val MAX_LENGTH_NAME = 30

object ValidationMessages {
    const val FIRST_NAME_EMPTY = "First name can't be empty"
    const val MIDDLE_NAME_EMPTY = "Middle name can't be empty"
    const val FATHER_LAST_NAME_EMPTY = "Father last name can't be empty"
    const val MOTHER_LAST_NAME_EMPTY = "Mother last name can't be empty"
    const val USER_ROLE_EMPTY = "Role can't be empty"
    const val PASSWORD_EMPTY = "Password can't be empty"
    const val TOKEN_EMPTY = "Token can't be empty"

    const val EMAIL_INVALID = "Invalid email"

    const val PASSWORD_SIZE = "Password must be at least $MIN_LENGTH_PASSWORD characters and at most $MAX_LENGTH_PASSWORD characters"
    const val FIRST_NAME_SIZE = "First name must be at least $MIN_LENGTH_NAME characters and at most $MAX_LENGTH_NAME characters"
    const val MIDDLE_NAME_SIZE = "Middle name must be at least $MIN_LENGTH_NAME characters and at most $MAX_LENGTH_NAME characters"
    const val FATHER_LAST_NAME_SIZE = "Father last name must be at least $MIN_LENGTH_NAME characters and at most $MAX_LENGTH_NAME characters"
    const val MOTHER_LAST_NAME_SIZE = "Mother last name must be at least $MIN_LENGTH_NAME characters and at most $MAX_LENGTH_NAME characters"
}

data class Name(
    @field:NotBlank(message = ValidationMessages.FIRST_NAME_EMPTY)
    @field:Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = ValidationMessages.FIRST_NAME_SIZE)
    val firstName: String,

    @field:NullableNotBlank(message = ValidationMessages.MIDDLE_NAME_EMPTY)
    @field:Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = ValidationMessages.MIDDLE_NAME_SIZE)
    val middleName: String?,

    @field:NotBlank(message = ValidationMessages.FATHER_LAST_NAME_EMPTY)
    @field:Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = ValidationMessages.FATHER_LAST_NAME_SIZE)
    val fatherLastName: String,

    @field:NotBlank(message = ValidationMessages.MOTHER_LAST_NAME_EMPTY)
    @field:Size(min = MIN_LENGTH_NAME, max = MAX_LENGTH_NAME, message = ValidationMessages.MOTHER_LAST_NAME_SIZE)
    val motherLastName: String
)

data class Token(
    @field:NotBlank(message = ValidationMessages.TOKEN_EMPTY)
    val token: String,
)

@Document("users")
data class User(
    @Id
    val id: String?,

    @field:Valid
    val name: Name,

    val email: String,

    val role: USER_ROLE,

    val password: String,

    val token: String,

    val incidents: List<String>?
)

data class CreateUser(
    @field:Valid
    val name: Name,

    val role: USER_ROLE,

    @field:Email(message = ValidationMessages.EMAIL_INVALID)
    val email: String,

    @field:NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
    @field:Size(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD, message = ValidationMessages.PASSWORD_SIZE)
    val password: String
)

data class LoginRequest(
    @field:Email(message = ValidationMessages.EMAIL_INVALID)
    val email: String,

    @field:NotBlank(message = ValidationMessages.PASSWORD_EMPTY)
    @field:Size(min = MIN_LENGTH_PASSWORD, max = MAX_LENGTH_PASSWORD, message = ValidationMessages.PASSWORD_SIZE)
    val password: String
)

data class LogoutRequest(
    @field:NotBlank(message = ValidationMessages.TOKEN_EMPTY)
    val token: String
)

data class UpdateUserRequest(
    @field:Email(message = ValidationMessages.EMAIL_INVALID)
    val email: String? = null,

    @field:Size(min = MIN_LENGTH_PASSWORD, message = ValidationMessages.PASSWORD_SIZE)
    val password: String? = null,

    @field:Valid
    val name: Name? = null,
)
