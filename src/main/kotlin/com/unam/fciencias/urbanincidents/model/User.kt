package com.unam.fciencias.urbanincidents.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
    @Id
    val id: String?,
    val email: String,
    val password: String,
    val token: String
)

data class CreateUser(
    val email: String,
    val password: String,
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)
