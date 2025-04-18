package com.unam.fciencias.urbanincidents.exception

class UserNotFoundException: NoSuchElementException{
    constructor() : super("The user was not found")

    constructor(message: String): super(message)
}
