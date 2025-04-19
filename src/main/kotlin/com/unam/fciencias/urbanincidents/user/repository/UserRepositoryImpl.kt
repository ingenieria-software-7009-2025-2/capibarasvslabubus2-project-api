package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.user.model.Name
import com.unam.fciencias.urbanincidents.user.model.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val mongoTemplate: MongoTemplate) : UserRepositoryCustom {

    private object PropertyNames {
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val TOKEN = "token"
        const val NAME = "name"
        const val INCIDENTS = "incidents"
    }

    override fun updateEmailById(id: String, email: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.EMAIL, email)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updatePasswordById(id: String, password: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.PASSWORD, password)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateTokenById(id: String, token: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.TOKEN, token)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateNameById(id: String, name: Name) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.NAME, name)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateIncidentsById(id: String, incidents: List<String>) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.INCIDENTS, incidents)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }
}
