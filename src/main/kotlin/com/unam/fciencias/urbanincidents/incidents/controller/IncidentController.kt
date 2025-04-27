package com.unam.fciencias.urbanincidents.incident.controller

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.service.IncidentService
import com.unam.fciencias.urbanincidents.user.model.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * REST controller for managing incidents. Provides endpoints to create, update, and retrieve
 * incidents, including image handling with multipart form data.
 */
@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/v1/incidents")
class IncidentController(private val incidentService: IncidentService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves an incident by its ID.
     *
     * @param id The unique ID of the incident.
     * @return A [ResponseEntity] containing the incident if found.
     * @throws InvalidIncidentIdException If the ID is blank.
     */
    @GetMapping("/{id}")
    fun getIncidentsById(@PathVariable id: String): ResponseEntity<Incident> {
        if (id.isBlank()) {
            throw InvalidIncidentIdException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(incidentService.getIncidentById(id))
    }

    /**
     * Retrieves a list of incidents filter by types, states and the archived status.
     *
     * @param types The list of types to search.
     * @param states The list of states to search.
     * @param archived The status archived to search.
     * @return The list with the incidnets that match the criteria.
     */
    @GetMapping
    fun getFilterIncidents(
            @RequestParam(required = false) type: List<INCIDENT_TYPE>?,
            @RequestParam(required = false) state: List<INCIDENT_STATE>?,
            @RequestParam(required = false, defaultValue = "false") archived: Boolean
    ): ResponseEntity<List<Incident>> {
        val safeTypes: List<INCIDENT_TYPE> = type ?: emptyList<INCIDENT_TYPE>()
        val safeStates: List<INCIDENT_STATE> = state ?: emptyList<INCIDENT_STATE>()
        return ResponseEntity.status(HttpStatus.OK)
                .body(incidentService.getFilterIncidents(safeTypes, safeStates, archived))
    }

    /**
     * Creates a new incident along with its associated images.
     *
     * @param incidentInfo The metadata and information about the incident.
     * @param images A list of image files associated with the incident.
     * @return A [ResponseEntity] containing the created incident.
     * @throws InvalidImagesListException If the images list is empty or contains invalid files.
     */
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createIncidents(
            @Valid @RequestPart("incident") incidentInfo: CreateIncidentRequest,
            @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Incident> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.createIncident(incidentInfo, images))
    }

    /**
     * Updates an existing incident and optionally replaces its images.
     *
     * @param updateRequest The data to be updated for the incident.
     * @param images An optional list of new images to replace the existing ones.
     * @return A [ResponseEntity] containing the updated incident.
     */
    @PatchMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun patchIncidents(
            @Valid @RequestPart("incident") updateRequest: PatchIncidentRequest,
            @RequestParam("images") images: List<MultipartFile>?
    ): ResponseEntity<Incident> {
        return ResponseEntity.status(HttpStatus.OK)
                .body(incidentService.patchIncident(updateRequest, images))
    }
}
