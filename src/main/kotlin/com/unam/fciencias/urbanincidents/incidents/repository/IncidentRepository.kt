package com.unam.fciencias.urbanincidents.incident.repository

import com.unam.fciencias.urbanincidents.incident.model.Incident
import org.springframework.data.mongodb.repository.MongoRepository

interface IncidentRepository : MongoRepository<Incident, String>, IncidentRepositoryCustom {}
