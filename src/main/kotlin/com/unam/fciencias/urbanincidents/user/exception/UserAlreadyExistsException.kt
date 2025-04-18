package com.unam.fciencias.urbanincidents.exception

class UserAlreadyExistsException(email: String) : IllegalStateException("User with email $email already exists")
