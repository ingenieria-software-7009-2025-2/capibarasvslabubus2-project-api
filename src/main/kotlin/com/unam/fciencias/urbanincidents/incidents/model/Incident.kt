package com.unam.fciencias.urbanincidents.incident.model

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.validator.*
import jakarta.validation.constraints.*
import java.time.LocalDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.mapping.Document

const val MIN_LENGTH_DESCRIPTION = 10
const val MAX_LENGHT_DESCRIPTION = 200

object ValidationMessages {
        const val INCIDENT_STATE_EMPTY = "The state can't be empty"
        const val OWNER_ID_EMTPY = "The owner id can't be empty"
        const val INCIDENT_TYPE_EMPTY = "The type of the incident can't be empty"
        const val LIST_IMAGES_EMPTY = "The list of images can't be empty"
        const val DESCRIPTION_EMPTY = "The description of the incident can't be empty"
        const val USER_TOKEN_EMTPY = "The user token can't be empy"

        const val DESCRIPTION_SIZE =
                "Description must be at least $MIN_LENGTH_DESCRIPTION characters and at most $MAX_LENGHT_DESCRIPTION characters"
}

data class SpecificState(
        val date: LocalDate?,
        val images: List<String>?,
)

data class States(
        val reported: SpecificState,
        val inProgress: SpecificState?,
        val solved: SpecificState?,
)

@Document("incidents")
data class Incident(
        @Id val id: String?,
        val ownerId: String,
        val type: INCIDENT_TYPE,
        val states: States,
        val description: String,
        val location: GeoJsonPoint,
        val archived: Boolean,
)

data class CreateIncidentRequest(
        val ownerId: String,
        val type: INCIDENT_TYPE,
        @field:NotBlank(message = ValidationMessages.DESCRIPTION_EMPTY)
        @field:Size(
                min = MIN_LENGTH_DESCRIPTION,
                max = MAX_LENGHT_DESCRIPTION,
                message = ValidationMessages.DESCRIPTION_SIZE
        )
        val description: String,
        val location: GeoJsonPoint,
)

data class PatchIncidentRequest(
        val id: String,
        @field:NotBlank(message = ValidationMessages.USER_TOKEN_EMTPY) val userToken: String,
        val state: INCIDENT_STATE,
        val type: INCIDENT_TYPE?,
        @field:NullableNotBlank(message = ValidationMessages.DESCRIPTION_EMPTY)
        @field:Size(
                min = MIN_LENGTH_DESCRIPTION,
                max = MAX_LENGHT_DESCRIPTION,
                message = ValidationMessages.DESCRIPTION_SIZE
        )
        val description: String?,
        val date: LocalDate?,
        val location: GeoJsonPoint?,
        val archived: Boolean,
)
