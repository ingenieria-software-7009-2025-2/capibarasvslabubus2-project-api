package com.unam.fciencias.urbanincidents.exception

class TokenEmptyOrNullException : IllegalArgumentException{
    constructor(): super("The token is empty or null")
}
