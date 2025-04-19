package com.unam.fciencias.urbanincidents.user.controller

import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.user.model.CreaterUserRequest
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.LogoutRequest
import com.unam.fciencias.urbanincidents.user.model.PatchUserRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<User> {
        if (id.isBlank()) {
            throw InvalidUserIdException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id))
    }

    @GetMapping()
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers())
    }

    @GetMapping("/me")
    fun getUserInfo(@RequestHeader("Authorization") token: String?): ResponseEntity<User> {
        if (token.isNullOrEmpty()) {
            throw TokenEmptyOrNullException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token))
    }

    @PostMapping
    fun createUser(@Valid @RequestBody user: CreaterUserRequest): ResponseEntity<User> =
            ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user))

    @PostMapping("/batch")
    fun createUsers(
            @Valid @RequestBody users: List<CreaterUserRequest>
    ): ResponseEntity<List<User>> {
        val createdUsers = users.map { userService.createUser(it) }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers)
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<User?> {
        val myUser = userService.loginUser(loginRequest)
        return ResponseEntity.status(HttpStatus.OK).body(myUser)
    }

    @PostMapping("/logout")
    fun logoutUser(@Valid @RequestBody logoutRequest: LogoutRequest): ResponseEntity<String?> {
        if (logoutRequest.token.isEmpty()) {
            throw TokenEmptyOrNullException()
        }

        userService.logoutUser(logoutRequest.token)
        return ResponseEntity.status(HttpStatus.OK).body("Session closed")
    }

    @PatchMapping("/me")
    fun patchUser(
            @RequestHeader("Authorization") token: String?,
            @Valid @RequestBody updateRequest: PatchUserRequest
    ): ResponseEntity<User> {
        if (token.isNullOrEmpty()) {
            throw TokenEmptyOrNullException()
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.patchUserByToken(token, updateRequest))
    }
}
