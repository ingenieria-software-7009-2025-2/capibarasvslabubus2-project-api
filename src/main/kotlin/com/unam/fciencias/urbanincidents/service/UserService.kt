package com.unam.fciencias.urbanincidents.service

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.LoginRequest
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.repository.UserRepository
import org.springframework.stereotype.Service
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
}
