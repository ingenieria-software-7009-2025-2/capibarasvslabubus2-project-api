package com.unam.fciencias.urbanincidents.incident.repository

import com.unam.fciencias.urbanincidents.incident.model.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class IncidentRepositoryImpl(private val mongoTemplate: MongoTemplate) : IncidentRepositoryCustom {

      object PropertyNames {
          const val STATE = "state"
      }


      override fun updateStateById(id: String, state: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.STATE, state)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }
}
