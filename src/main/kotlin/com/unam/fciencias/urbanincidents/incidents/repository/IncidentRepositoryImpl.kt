package com.unam.fciencias.urbanincidents.incident.repository

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.incident.model.*
import java.time.LocalDate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class IncidentRepositoryImpl(private val mongoTemplate: MongoTemplate) : IncidentRepositoryCustom {

    object PropertyNames {
        const val STATES = "states"
        const val IMAGES = "images"
        const val DATE = "date"
        const val LOCATION = "location"
        const val TYPE = "type"
        const val DESCRIPTION = "description"
    }

    override fun updateStateImagesById(id: String, images: List<String>, state: INCIDENT_STATE) {
        val query = Query(Criteria.where("_id").`is`(id))

        val nameField = "${PropertyNames.STATES}.${state.toFieldTag()}.${PropertyNames.IMAGES}"

        val update = Update().set(nameField, images)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }

    override fun updateStateDateById(id: String, date: LocalDate, state: INCIDENT_STATE) {
        val query = Query(Criteria.where("_id").`is`(id))

        val nameField = "${PropertyNames.STATES}.${state.toFieldTag()}.${PropertyNames.DATE}"

        val update = Update().set(nameField, date)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }

    override fun updatedDescriptionById(id: String, description: String) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.DESCRIPTION, description)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }

    override fun updateLocationById(id: String, location: GeoJsonPoint) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.LOCATION, location)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }

    override fun updateTypeById(id: String, type: INCIDENT_TYPE) {
        val query = Query(Criteria.where("_id").`is`(id))
        val update = Update().set(PropertyNames.TYPE, type)
        mongoTemplate.updateFirst(query, update, Incident::class.java)
    }

    private fun INCIDENT_STATE.toFieldTag(): String =
            when (this) {
                INCIDENT_STATE.REPORTED -> "reported"
                INCIDENT_STATE.IN_PROGRESS -> "inProgress"
                INCIDENT_STATE.SOLVED -> "solved"
            }
}
