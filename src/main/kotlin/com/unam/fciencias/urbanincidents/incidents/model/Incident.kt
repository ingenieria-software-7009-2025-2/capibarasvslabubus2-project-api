package com.unam.fciencias.urbanincidents.incident.model

import jakarta.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("incidents")
data class Incident(
        @Id val id: String?,
        val state: String,
        val owner: String,
        val type: String,

// dates
// coordinates
// photos
)
