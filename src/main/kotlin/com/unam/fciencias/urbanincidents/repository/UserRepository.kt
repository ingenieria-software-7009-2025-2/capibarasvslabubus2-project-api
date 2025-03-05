package com.unam.fciencias.urbanincidents.repository

import com.unam.fciencias.urbanincidents.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update

interface UserRepository : MongoRepository<User, String>{
    // Método para buscar un usuario por token
    fun findByToken(token: String): User?

    // Método para actualizar email por ID
    @Query("{ 'id' : ?0 }")
    @Update("{ '\$set' : { 'email' : ?1 } }")
    fun updateEmailById(id: String, email: String)

    // Método para actualizar password por ID
    @Query("{ 'id' : ?0 }")
    @Update("{ '\$set' : { 'password' : ?1 } }")
    fun updatePasswordById(id: String, password: String)
}