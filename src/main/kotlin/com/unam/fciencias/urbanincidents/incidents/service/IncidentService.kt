package com.unam.fciencias.urbanincidents.incident.service

import com.unam.fciencias.urbanincidents.incident.repository.IncidentRepository
import org.springframework.stereotype.Service

@Service class IncidentService(private val incidentRepository: IncidentRepository) {}
