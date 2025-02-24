package com.unam.fciencias.urbanincidents.controller

import com.unam.fciencias.urbanincidents.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/users")
class UserController {

    /**
     * Endpoint for creating a new user.
     * This method handles HTTP POST requests to create a new user in the system.
     * @param user The user object received in the request body containing the user's details
     *             such as email, password, and token.
     * @return ResponseEntity containing the created User object in the response body
     *         with HTTP status 200 (OK).
     */
    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val myUser = User(user.email, user.password, user.token)
        return ResponseEntity.ok(myUser)
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
