package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.model.Name

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : UserRepositoryCustom {

    override fun updateEmailById(id: String, email: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set("email", email)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updatePasswordById(id: String, password: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set("password", password)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateTokenById(id: String, token: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set("token", token)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateNameById(id: String, name: Name) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set("name", name)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }
}
