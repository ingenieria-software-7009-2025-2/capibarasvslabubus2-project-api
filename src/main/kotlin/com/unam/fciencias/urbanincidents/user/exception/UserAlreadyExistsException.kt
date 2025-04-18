package com.unam.fciencias.urbanincidents.exception

class UserAlreadyExistsException : IllegalStateException{
    constructor(): super("This user is already registered")

    constructor(email: String) : super("User with email $email already exists")
}
