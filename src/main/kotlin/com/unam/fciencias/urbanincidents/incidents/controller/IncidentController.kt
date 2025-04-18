package com.unam.fciencias.urbanincidents.incident.controller

import com.unam.fciencias.urbanincidents.incident.service.IncidentService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/v1/incidents")
class IncidentController(private val userService: IncidentService) {}
