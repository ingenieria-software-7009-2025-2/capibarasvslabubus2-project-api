package com.unam.fciencias.urbanincidents.service

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.LoginRequest
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.repository.UserRepository
import org.springframework.stereotype.Service
import com.unam.fciencias.urbanincidents.controller.body.UpdateUserRequest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUser): User {
        val user = User(id = null, email = request.email, password = request.password, token = request.token)
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
        ) ?: return null
        // Update token
        val token = UUID.randomUUID().toString()
        userRepository.updateTokenById(user.id.toString(), token)
        // Return user with new token
        val myUser = User(
            id = user.id.toString(),
            email = user.email,
            token = token,
            password = user.password
        )
        return myUser
    }

    /**
     * This method finds a user by token, then invalidates the token to logout.
     * @param token the token of the user that is closing session.
     * @return true if the user was found.
     */
    fun logoutUser(token: String): Boolean {
        val userFound = userRepository.findByToken(token);
        if(userFound != null)
            userRepository.updateTokenById(userFound.id.toString(), "")
        return (userFound != null)
    }

    fun updateUserByToken(token: String, updateRequest: UpdateUserRequest): User? {
        // Buscar usuario por token
        val user = userRepository.findByToken(token) ?: return null

        // Actualizar campos específicos usando consultas del repositorio
        if (updateRequest.email != null && user.id != null) {
            userRepository.updateEmailById(user.id, updateRequest.email)
        }

        if (updateRequest.password != null && user.id != null) {
            userRepository.updatePasswordById(user.id, updateRequest.password)
        }

        // Buscar y devolver el usuario actualizado
        return userRepository.findByToken(token)
    }


    fun getUser(token: String?): User {
        if (token.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado")
        }
        val user = userRepository.findByToken(token) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido")
        return user
    }

}
