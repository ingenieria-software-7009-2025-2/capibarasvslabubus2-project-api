package com.unam.fciencias.urbanincidents.repository

import com.unam.fciencias.urbanincidents.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update

interface UserRepository : MongoRepository<User, String>
{
    fun findByEmailAndPassword(email: String, password: String): User?

    @Query("{ 'id' : ?0 }")
    @Update("{ '\$set' : { 'token' : ?1 } }")
    fun updateTokenById(id: String, token: String)
}