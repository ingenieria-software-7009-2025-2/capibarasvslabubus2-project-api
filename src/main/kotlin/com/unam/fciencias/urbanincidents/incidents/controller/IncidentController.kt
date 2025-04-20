package com.unam.fciencias.urbanincidents.incident.controller

import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.service.IncidentService
import com.unam.fciencias.urbanincidents.user.model.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
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

    /**
     * Retrieves an incident by its ID.
     *
     * @param id The unique ID of the incident.
     * @return A [ResponseEntity] containing the incident if found.
     * @throws InvalidIncidentIdException If the ID is blank.
     */
    @GetMapping("/{id}")
    fun getIncidentById(@PathVariable id: String): ResponseEntity<Incident> {
        if (id.isBlank()) {
            throw InvalidIncidentIdException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(incidentService.getIncidentById(id))
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
    fun createIncident(
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
    fun patchIncident(
            @Valid @RequestPart("incident") updateRequest: PatchIncidentRequest,
            @RequestParam("images") images: List<MultipartFile>?
    ): ResponseEntity<Incident> {
        return ResponseEntity.status(HttpStatus.OK)
                .body(incidentService.patchIncident(updateRequest, images))
    }
}
