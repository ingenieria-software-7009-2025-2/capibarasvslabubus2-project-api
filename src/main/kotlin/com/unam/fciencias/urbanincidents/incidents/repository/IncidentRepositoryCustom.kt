package com.unam.fciencias.urbanincidents.incident.repository

interface IncidentRepositoryCustom {

  fun updateStateById(id: String, state: String)

}
