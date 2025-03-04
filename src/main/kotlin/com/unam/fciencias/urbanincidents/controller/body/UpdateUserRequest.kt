package com.unam.fciencias.urbanincidents.controller.body

data class UpdateUserRequest (
    val email: String? = null,
    val password: String? = null
)