package com.unam.fciencias.urbanincidents.user.controller

import com.unam.fciencias.urbanincidents.user.model.CreateUser
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.model.UpdateUserRequest
import com.unam.fciencias.urbanincidents.user.model.LogoutRequest
import com.unam.fciencias.urbanincidents.user.model.Token
import com.unam.fciencias.urbanincidents.user.service.UserService
import com.unam.fciencias.urbanincidents.exception.UserAlreadyExistsException
import com.unam.fciencias.urbanincidents.exception.InvalidTokenException
import com.unam.fciencias.urbanincidents.exception.TokenEmptyOrNullException

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated

import jakarta.validation.Valid

import org.slf4j.Logger
import org.slf4j.LoggerFactory; 


@RestController
@CrossOrigin(origins = ["http://localhost:5173"]) 
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(this::class.java); 

    /**
    * Endpoint for creating a new user.
    * This method handles HTTP POST requests to create a new user in the system.
    * @param user The user object received in the request body containing the user's details
    *             such as email and password.
    * @return ResponseEntity containing the created User object in the response body
    *         with HTTP status 201 (Created).
    */
    @PostMapping
    fun createUser(@Valid @RequestBody user: CreateUser): ResponseEntity<User> = ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.createUser(user))

    @PostMapping("/batch")
    fun createUsers(@Valid @RequestBody users: List<CreateUser>): ResponseEntity<List<User>> {
        val createdUsers = users.map { userService.createUser(it) }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers)
    }


    /**
     * Endpoint for user login.
     * This method handles HTTP POST requests to give a user access into their account.
     * @param loginRequest A JSON with the fields of mail and password.
     * @return ResponseEntity containing the found user with the token updated in HTTP
     *         status 200 (OK). Otherwise, with HTTP status 404 and not found.
     */
    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<User?> {
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
    fun logoutUser(@Valid @RequestBody logoutRequest: LogoutRequest): ResponseEntity<String?> {
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
        if(token.isNullOrEmpty()){
            throw TokenEmptyOrNullException(); 
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUser(token))
    }

    @PutMapping("/me")
    fun updateCurrentUser(
        @RequestHeader("Authorization") token: String?, @Valid
        @RequestBody updateRequest:  UpdateUserRequest
    ): ResponseEntity<User> {
        if (token.isNullOrEmpty()) {
            throw TokenEmptyOrNullException(); 
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.updateUserByToken(token, updateRequest))
    }
}
