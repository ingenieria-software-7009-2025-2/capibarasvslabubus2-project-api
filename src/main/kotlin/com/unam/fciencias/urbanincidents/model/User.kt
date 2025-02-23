package com.unam.fciencias.urbanincidents.model

data class User(
    val email: String,
    val password: String,
    val token: String
)