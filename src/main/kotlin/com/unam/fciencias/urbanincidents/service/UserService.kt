package com.unam.fciencias.urbanincidents.service

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUser): User {
        val user = User(id = null, email = request.email, password = request.password, token = request.token)
        return userRepository.save(user)
    }
    fun loginUser(loginRequest: Map<String, String>): User? {
        if(loginRequest["email"] != null && loginRequest["password"] != null) {
            val user = userRepository.findByEmailAndPassword(
                loginRequest["email"].toString(),
                loginRequest["password"].toString()
            ) ?: return null
            return user
        }
        return null
    }
}
