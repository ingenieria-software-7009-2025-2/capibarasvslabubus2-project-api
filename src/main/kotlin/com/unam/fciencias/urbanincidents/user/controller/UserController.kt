package com.unam.fciencias.urbanincidents.user.controller

import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.user.model.CreaterUserRequest
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.PatchUserRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing users in the system. Handles operations such as user creation,
 * authentication, updating, and retrieval.
 */
@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/v1/users")
class UserController(private val userService: UserService) {

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user ID.
     * @return The user associated with the given ID.
     * @throws InvalidUserIdException if the ID is blank.
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<User> {
        if (id.isBlank()) {
            throw InvalidUserIdException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id))
    }

    /**
     * Retrieves all registered users.
     *
     * @return A list of users.
     */
    @GetMapping()
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers())
    }

    /**
     * Retrieves the currently authenticated user's information based on the provided token.
     *
     * @param token The token from the Authorization header.
     * @return The user corresponding to the token.
     * @throws TokenEmptyOrNullException if the token is missing or empty.
     */
    @GetMapping("/me")
    fun getUserInfo(@RequestHeader("Authorization") token: String?): ResponseEntity<User> {
        val validatedToken = validToken(token)
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(validatedToken))
    }

    /**
     * Creates a new user.
     *
     * @param user The request body containing the user's data.
     * @return The newly created user.
     */
    @PostMapping
    fun createUser(@Valid @RequestBody user: CreaterUserRequest): ResponseEntity<User> =
            ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user))

    /**
     * Creates multiple users in batch.
     *
     * @param users A list of user creation requests.
     * @return A list of newly created users.
     */
    @PostMapping("/batch")
    fun createUsers(
            @Valid @RequestBody users: List<CreaterUserRequest>
    ): ResponseEntity<List<User>> {
        val createdUsers = users.map { userService.createUser(it) }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers)
    }

    /**
     * Logs in a user.
     *
     * @param loginRequest The login credentials.
     * @return The logged-in user's information.
     */
    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<User?> {
        val myUser = userService.loginUser(loginRequest)
        return ResponseEntity.status(HttpStatus.OK).body(myUser)
    }

    /**
     * Logs out a user by invalidating their token.
     *
     * @param logoutRequest The request containing the token to invalidate.
     * @return A confirmation message upon successful logout.
     * @throws TokenEmptyOrNullException if the token is missing or empty.
     */
    @PostMapping("/logout")
    fun logoutUser(@Valid @RequestHeader("Authorization") token: String?): ResponseEntity<String?> {
        val validatedToken = validToken(token)
        userService.logoutUser(validatedToken)
        return ResponseEntity.status(HttpStatus.OK).body("Session closed")
    }

    /**
     * Partially updates the authenticated user's data.
     *
     * @param token The token from the Authorization header.
     * @param updateRequest The fields to update.
     * @return The updated user.
     * @throws TokenEmptyOrNullException if the token is missing or empty.
     */
    @PatchMapping("/me")
    fun patchUser(
            @RequestHeader("Authorization") token: String?,
            @Valid @RequestBody updateRequest: PatchUserRequest
    ): ResponseEntity<User> {
        val validatedToken = validToken(token)
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.patchUserByToken(validatedToken, updateRequest))
    }

    /**
     * Delete and user by id.
     *
     * @param id The id of the user to eliminate
     * @return A 204 responde if the operation was successful.
     * @throws TokenEmptyOrNullException if the token is missing or empty.
     * @throws InvalidUserIdException if the if of the user is blank
     */
    @DeleteMapping("/{id}")
    fun deleteUser(
            @RequestHeader("Authorization") token: String?,
            @PathVariable id: String
    ): ResponseEntity<Any> {
        if (token.isNullOrEmpty()) {
            throw TokenEmptyOrNullException()
        }
        if (id.isBlank()) {
            throw InvalidUserIdException()
        }
        userService.deleteUserById(token, id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    /**
     * Check if the given token is not null or empty and if so, returns an excpetion.
     *
     * @param token The token to check.
     * @return The token if is not null or empty
     */
    private fun validToken(token: String?): String {
        if (token.isNullOrEmpty()) {
            throw TokenEmptyOrNullException()
        }
        return token
    }
}
