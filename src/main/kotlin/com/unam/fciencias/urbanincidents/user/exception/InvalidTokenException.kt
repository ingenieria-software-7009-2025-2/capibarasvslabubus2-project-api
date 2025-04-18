package com.unam.fciencias.urbanincidents.exception

class InvalidTokenException(token: String) : NoSuchElementException("There is no user associated with the token $token")
