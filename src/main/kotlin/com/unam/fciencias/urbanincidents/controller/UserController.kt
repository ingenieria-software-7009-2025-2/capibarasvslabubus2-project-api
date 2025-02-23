package com.unam.fciencias.urbanincidents.controller

import com.unam.fciencias.urbanincidents.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/users")
class UserController {


    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        TODO("Por implementar")
    }

    // Endpoint for user login
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: Map<String, String>): ResponseEntity<User> {
        TODO("Por implementar")
    }

    // Endpoint for user logout
    @PostMapping("/logout")
    fun logoutUser(): ResponseEntity<String> {
        TODO("Por implementar")
    }

    // Endpoint to retrieve user information
    @GetMapping("/me")
    fun getUserInfo(): ResponseEntity<User> {
        TODO("Por implementar")
    }
}
