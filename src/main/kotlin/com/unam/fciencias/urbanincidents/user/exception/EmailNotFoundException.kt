package com.unam.fciencias.urbanincidents.exception

class EmailNotFoundException(email: String) : NoSuchElementException("There is no user with the email: $email")
