package com.unam.fciencias.urbanincidents.incident.repository

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.incident.model.*
import java.time.LocalDate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

interface IncidentRepositoryCustom {
  fun updateStateImagesById(id: String, images: List<String>, state: INCIDENT_STATE)
  fun updatedDescriptionById(id: String, description: String)
  fun updateStateDateById(id: String, date: LocalDate, state: INCIDENT_STATE)
  fun updateTypeById(id: String, type: INCIDENT_TYPE)
  fun updateLocationById(id: String, location: GeoJsonPoint)
  fun updateArchivedStateById(id: String, archivedState: Boolean)
  fun getFilterIncidents(
          types: List<INCIDENT_TYPE>,
          states: List<INCIDENT_STATE>,
          archived: Boolean
  ): List<Incident>
  fun deleteIncident(id: String)
}
