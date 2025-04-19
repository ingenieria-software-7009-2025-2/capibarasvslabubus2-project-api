package com.unam.fciencias.urbanincidents.incident.repository

import com.unam.fciencias.urbanincidents.enums.*
import java.time.LocalDate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

interface IncidentRepositoryCustom {
  fun updateStateImagesById(id: String, images: List<String>, state: INCIDENT_STATE)
  fun updatedDescriptionById(id: String, description: String)
  fun updateStateDateById(id: String, date: LocalDate, state: INCIDENT_STATE)
  fun updateTypeById(id: String, type: INCIDENT_TYPE)
  fun updateLocationById(id: String, location: GeoJsonPoint)
}
