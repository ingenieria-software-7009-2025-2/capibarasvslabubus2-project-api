package com.unam.fciencias.urbanincidents.repository

import com.unam.fciencias.urbanincidents.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>
{
    fun findByEmailAndPassword(email: String, password: String): User?
}