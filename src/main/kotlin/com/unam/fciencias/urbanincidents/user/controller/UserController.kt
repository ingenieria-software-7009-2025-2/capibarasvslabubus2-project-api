package com.unam.fciencias.urbanincidents.user.controller

import com.unam.fciencias.urbanincidents.user.model.CreateUser
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.controller.body.UpdateUserRequest
import com.unam.fciencias.urbanincidents.user.model.LogoutRequest
import com.unam.fciencias.urbanincidents.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:5173"]) 
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    /**
     * Endpoint for creating a new user.
     * This method handles HTTP POST requests to create a new user in the system.
     * @param user The user object received in the request body containing the user's details
     *             such as email and password.
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
     * @return If token is found, ResponseEntity containing a message confirming closed
     *         session with HTTP status 200 (OK). Otherwise, contains an error message.
     */
    @PostMapping("/logout")
    fun logoutUser(@RequestBody logoutRequest: LogoutRequest): ResponseEntity<String?> {
        if (logoutRequest.token.isEmpty())
            return ResponseEntity.status(401).build()
        val successLogout = userService.logoutUser(logoutRequest.token)
        return if (!successLogout)
            ResponseEntity.badRequest().build()
        else
            ResponseEntity.ok("Sesión cerrada")
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
