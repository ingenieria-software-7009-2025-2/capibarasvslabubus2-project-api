package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.user.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>, UserRepositoryCustom {
    fun findByToken(token: String): User?
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findByEmail(email: String): User?
}
