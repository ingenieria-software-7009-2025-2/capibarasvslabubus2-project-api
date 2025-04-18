package com.unam.fciencias.urbanincidents.exception

class UserNotFoundException(message: String = "Invalid email or password") : NoSuchElementException(message)
