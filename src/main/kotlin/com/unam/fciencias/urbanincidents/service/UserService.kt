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
}
