package com.unam.fciencias.urbanincidents.service

import com.unam.fciencias.urbanincidents.model.CreateUser
import com.unam.fciencias.urbanincidents.model.User
import com.unam.fciencias.urbanincidents.repository.UserRepository
import org.springframework.stereotype.Service
import com.unam.fciencias.urbanincidents.controller.body.UpdateUserRequest

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(request: CreateUser): User {
        val user = User(id = null, email = request.email, password = request.password, token = request.token)
        return userRepository.save(user)
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

}
