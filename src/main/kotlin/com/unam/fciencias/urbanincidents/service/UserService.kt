package com.unam.fciencias.urbanincidents.service

import com.unam.fciencias.urbanincidents.model.CreateUser
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
     * Endpoint for user login.
     * This method handles HTTP POST requests to give a user access into their account.
     * @param loginRequest A JSON with the fields of mail and password.
     * @return ResponseEntity containing the found user with the token updated in HTTP
     *         status 200 (OK). Otherwise, with HTTP status 404 and not found.
     */
    fun loginUser(loginRequest: Map<String, String>): User? {
        if(loginRequest["email"] != null && loginRequest["password"] != null) {
            // Search user
            val user = userRepository.findByEmailAndPassword(
                loginRequest["email"].toString(),
                loginRequest["password"].toString()
            ) ?: return null
            // Update token
            val token = UUID.randomUUID().toString()
            userRepository.updateTokenById(user.id.toString(), token)
            User(
                id = user.id.toString(),
                email = user.email,
                token = token,
                password = user.password
            )
            return user
        }
        return  null
    }
}
