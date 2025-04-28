package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.enums.USER_ROLE
import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.user.model.User
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

data class UserRoleProjection(val role: USER_ROLE?)

@Repository
class UserRepositoryImpl(private val mongoTemplate: MongoTemplate) : UserRepositoryCustom {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private object PropertyNames {
        const val ID = "_id"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val TOKEN = "token"
        const val INCIDENTS = "incidents"
    }

    override fun updateEmailById(id: String, email: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(id))
        val update = Update().set(PropertyNames.EMAIL, email)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updatePasswordById(id: String, password: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(id))
        val update = Update().set(PropertyNames.PASSWORD, password)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun updateTokenById(id: String, token: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(id))
        val update = Update().set(PropertyNames.TOKEN, token)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun findUserRoleByToken(token: String): USER_ROLE? {
        val query = Query(Criteria.where(PropertyNames.TOKEN).`is`(token))
        // implementar la proyeccion
        val roleOnly = mongoTemplate.findOne(query, User::class.java)
        return roleOnly?.role
    }

    override fun deleteUserById(id: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(id))
        mongoTemplate.remove(query, User::class.java)
    }

    override fun pushIncidentToUserList(userId: String, incidentId: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(userId))
        val update = Update().push(PropertyNames.INCIDENTS, incidentId)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun removeIncidentFromUserList(userId: String, incidentId: String) {
        val query = Query(Criteria.where(PropertyNames.ID).`is`(userId))
        val update = Update().pull(PropertyNames.INCIDENTS, incidentId)
        mongoTemplate.updateFirst(query, update, User::class.java)
    }

    override fun findUserIdByToken(token: String): String? {
        val query = Query(Criteria.where(PropertyNames.TOKEN).`is`(token))
        // implementar la proyeccion
        val idOnly = mongoTemplate.findOne(query, User::class.java)
        return idOnly?.id
    }
}
