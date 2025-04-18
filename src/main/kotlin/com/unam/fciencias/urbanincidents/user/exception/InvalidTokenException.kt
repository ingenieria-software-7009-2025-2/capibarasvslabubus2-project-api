package com.unam.fciencias.urbanincidents.exception

class InvalidTokenException : NoSuchElementException {
    constructor() : super("The given token is invalid")

    constructor(token: String) : super("There is no user associated with the token $token")
}
