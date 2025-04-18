package com.unam.fciencias.urbanincidents.incident.repository

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class IncidentRepositoryImpl(private val mongoTemplate: MongoTemplate) : IncidentRepositoryCustom {}
