package com.unam.fciencias.urbanincidents.user.controller.body

data class UpdateUserRequest (
    val email: String? = null,
    val password: String? = null
)