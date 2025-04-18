package com.unam.fciencias.urbanincidents.exception

class EmailNotFoundException : NoSuchElementException {
    constructor() : super("The email was not found")

    constructor(email: String) : super("There is no user with the email: $email")
}
