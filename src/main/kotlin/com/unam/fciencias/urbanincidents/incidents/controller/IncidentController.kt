package com.unam.fciencias.urbanincidents.incident.controller

import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.service.IncidentService
import com.unam.fciencias.urbanincidents.user.model.*
import com.unam.fciencias.urbanincidents.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import jakarta.validation.constraints.*

@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/v1/incidents")
class IncidentController(
        private val incidentService: IncidentService
) {

    @GetMapping("/{id}")
    fun getIncidentById(@PathVariable id: String): ResponseEntity<Incident> {
        if (id.isBlank()) {
            throw InvalidIncidentIdException()
        }
        return ResponseEntity.status(HttpStatus.OK).body(incidentService.getIncidentById(id))
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createIncident(
            @Valid @RequestPart("incident") incidentInfo: CreateIncident,
            @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Incident> {
        validateImagesList(images)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.createIncident(incidentInfo, images))
    }


    // @PutMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    // fun updateIncident(@Valid @RequestPart("incident") updateRequest : UpdateIncident, @RequestParam("images") images : List<MultipartFile>?) : ResponseEntity<Incident> {
    //     return ResponseEntity.status(HttpStatus.OK).body(incidentService.updateIncident(updateRequest, images))
    // }

    fun validateImagesList(images: List<MultipartFile>) : Unit {
        if (images.isEmpty()) {
            throw InvalidImagesListException("The list of incident images can't be empty")
        }
        for(image in images){
            if(image.isEmpty()){
                throw InvalidImagesListException("The list can't contain an empty image")
            }
        }
        return 
    }

}
