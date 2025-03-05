package com.unam.fciencias.urbanincidents.controller

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.LoginRequest
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.controller.body.UpdateUserRequest
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
     * @return ResponseEntity containing the found user with the token updated in HTTP
     *         status 200 (OK). Otherwise, with HTTP status 404 and not found.
     */
    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<User?> {
        val myUser = userService.loginUser(loginRequest)
        return if (myUser == null)
            ResponseEntity.notFound().build()
        else
            ResponseEntity.ok(myUser)
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


    @PutMapping("/me")
    fun updateCurrentUser(
        @RequestHeader("Authorization") token: String?,
        @RequestBody updateRequest:  UpdateUserRequest
    ): ResponseEntity<User> {
        if (token.isNullOrEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            return ResponseEntity.status(401).build()
        }

        val updatedUser = userService.updateUserByToken(token, updateRequest)

        return if (updatedUser != null) {
            ResponseEntity.ok(updatedUser)
        } else {
            ResponseEntity.status(404).build()
        }

    }
}
