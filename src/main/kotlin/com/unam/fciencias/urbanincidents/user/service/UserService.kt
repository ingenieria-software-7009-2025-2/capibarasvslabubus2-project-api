package com.unam.fciencias.urbanincidents.user.service

import com.unam.fciencias.urbanincidents.user.model.CreateUser
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.model.UpdateUserRequest
import com.unam.fciencias.urbanincidents.user.model.Name
import com.unam.fciencias.urbanincidents.user.repository.UserRepository
import com.unam.fciencias.urbanincidents.exception.UserAlreadyExistsException
import com.unam.fciencias.urbanincidents.exception.InvalidTokenException
import com.unam.fciencias.urbanincidents.exception.TokenEmptyOrNullException
import com.unam.fciencias.urbanincidents.exception.UserNotFoundException
import com.unam.fciencias.urbanincidents.exception.EmailNotFoundException

import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * Creates a new user in the database.
     *
     * @param request The request object containing the user's email and password.
     * @return The saved user with a generated ID.
     */
    fun createUser(request: CreateUser): User {
        userRepository.findByEmail(request.email)?.let {
            throw UserAlreadyExistsException(request.email)
        }
        val user = User(
            id = null,
            name = request.name,
            role = request.role, 
            email = request.email,
            password = request.password,
            token = "", 
            incidents = null
        )
        return userRepository.save(user)
    }



    /**
     * This method finds a user by email and password, then updates their token to login.
     * @param loginRequest with the fields of mail and password.
     * @return the user updated if is found, otherwise returns null.
     */
    fun loginUser(loginRequest: LoginRequest): User? {
        // Search user
        val user = userRepository.findByEmailAndPassword(
            loginRequest.email,
            loginRequest.password
        ) ?: throw UserNotFoundException("The user was not found with the given email and password")

        // Update token

        val userId = user.id ?: throw IllegalStateException("User ID should not be null")

        val token = UUID.randomUUID().toString()
        userRepository.updateTokenById(userId, token)

        // Return user with new token
        val myUser = User(
            id = userId,
            name = user.name, 
            role = user.role, 
            email = user.email,
            token = token,
            password = user.password,
            incidents = user.incidents,
        )
        return myUser
    }

    /**
     * This method finds a user by token, then invalidates the token to logout.
     * @param token the token of the user that is closing session.
     * @return true if the user was found.
     */
    fun logoutUser(token: String): Unit {
        val userFound = userRepository.findByToken(token) ?: throw InvalidTokenException(token)
        userRepository.updateTokenById(userFound.id.toString(), "")
    }

    fun updateUserByToken(token: String, updateRequest: UpdateUserRequest): User {
        val user = userRepository.findByToken(token)
            ?: throw InvalidTokenException(token)

        val userId = user.id ?: throw IllegalStateException("User ID should not be null")

        // Si no hay cambios, no hacemos nada
        if (
            updateRequest.email == null &&
            updateRequest.password == null &&
            updateRequest.name == null
        ) return user

        // Validamos si el nuevo email ya está en uso por otro usuario
        updateRequest.email?.let { newEmail ->
            val existingUser = userRepository.findByEmail(newEmail)
            if (existingUser != null && existingUser.id != userId) {
                throw UserAlreadyExistsException(newEmail)
            }
            userRepository.updateEmailById(userId, newEmail)
        }

        updateRequest.password?.let {
            userRepository.updatePasswordById(userId, it)
        }

        updateRequest.name?.let {
            userRepository.updateNameById(userId, it)
        }

        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException() }
    }

    fun getUserByToken(token: String): User =
        userRepository.findByToken(token) ?: throw InvalidTokenException(token)

    fun getUserById(id: String): User =
        userRepository.findById(id).orElseThrow { UserNotFoundException() }

    fun getUserByEmail(email: String): User =
        userRepository.findByEmail(email) ?: throw EmailNotFoundException(email)
}
