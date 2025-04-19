package com.unam.fciencias.urbanincidents.incident.model

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.validator.*
import jakarta.validation.constraints.*
import java.time.LocalDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document

object ValidationMessages {
    const val INCIDENT_STATE_EMPTY = "The state can't be empty"
    const val OWNER_ID_EMTPY = "The owner id can't be empty"
    const val INCIDENT_TYPE_EMPTY = "The type of the incident can't be empty"

    const val LIST_IMAGES_EMPTY = "The list of images can't be empty"
}

data class PublicationDates(
    val reported: LocalDate?,
    val inProgress: LocalDate?,
    val resolution: LocalDate?,
)

@Document("incidents")
data class Incident(
    @Id val id: String? = null,
    val ownerId: String,
    val state: INCIDENT_STATE,
    val type: INCIDENT_TYPE,
    val images: List<String>,
    val dates: PublicationDates,
    val location: GeoJsonPoint
)

data class CreateIncident(
    @field:ValidState val state: INCIDENT_STATE,
    val ownerId: String,
    @field:ValidType val type: INCIDENT_TYPE,
    val location: GeoJsonPoint,
)

data class UpdateIncident(
    val id: String, 
    @field:ValidState
    val state: INCIDENT_STATE? = null,
    val dates: PublicationDates? = null,
)
