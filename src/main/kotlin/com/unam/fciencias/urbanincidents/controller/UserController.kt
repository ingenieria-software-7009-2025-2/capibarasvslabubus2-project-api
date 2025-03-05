package com.unam.fciencias.urbanincidents.controller

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    /**
     * Endpoint for creating a new user.
     * This method handles HTTP POST requests to create a new user in the system.
     * @param user The user object received in the request body containing the user's details
     *             such as email, password, and token.
     * @return ResponseEntity containing the created User object in the response body
     *         with HTTP status 200 (OK).
     */
    @PostMapping
    fun createUser(@RequestBody user: CreateUser): ResponseEntity<User> {
        val myUser = userService.createUser(user)
        return ResponseEntity.ok(myUser)
    }

    /**
     * Endpoint for user login.
     * This method handles HTTP POST requests to give a user access into their account.
     * @param loginRequest A JSON with the fields of mail and password.
     * @return ResponseEntity containing the created User object in the response body
     *         with HTTP status 200 (OK).
     */
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: Map<String, String>): ResponseEntity<User> {
        var mail = "no email received"
        if(loginRequest.containsKey("email"))
            mail = loginRequest["email"].toString()
        var password = "no password received"
        if(loginRequest.containsKey("password"))
            password = loginRequest["password"].toString()
        val myUser = User(null, mail, password, "random user")
        return ResponseEntity.ok(myUser)

    }

    /**
     * Endpoint for user logout.
     * This method handles HTTP POST requests to close a user session.
     * @return ResponseEntity containing a message confirming closed session
     *         with HTTP status 200 (OK).
     */
    @PostMapping("/logout")
    fun logoutUser(): ResponseEntity<String> {
        return ResponseEntity.ok("Sesión cerrada")
    }

    /**
     * Endpoint to retrieve user information.
     * This method handles HTTP GET requests to fetch the details from a user.
     *
     * @return a User object with the hardcoded information of a user.
     */
    @GetMapping("/me")
    fun getUserInfo(@RequestHeader("Authorization") token: String?): ResponseEntity<User> {
        val user = userService.getUser(token)
        return ResponseEntity.ok(user)
    }
}
