package com.unam.fciencias.urbanincidents.incident.service

import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.repository.IncidentRepository
import com.unam.fciencias.urbanincidents.user.model.*
import com.unam.fciencias.urbanincidents.user.service.UserService
import java.time.LocalDate
import java.util.Base64
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class IncidentService(
        private val incidentRepository: IncidentRepository,
        private val userService: UserService
) {


    fun getIncidentById(id: String) : Incident =  incidentRepository.findById(id).orElseThrow { IncidentNotFoundException() }

    fun createIncident(incidentInfo: CreateIncident, images: List<MultipartFile>): Incident {
        if (!userService.existsUserById(incidentInfo.ownerId)) {
            throw UserNotFoundException()
        }

        val encodedImages: List<String> = listOfEncodedImages(images)
        val publicationDates: PublicationDates =
                PublicationDates(
                        reported = LocalDate.now(),
                        inProgress = null,
                        resolution = null,
                )

        val newIncident: Incident =
                Incident(
                        id = null,
                        ownerId = incidentInfo.ownerId,
                        state = incidentInfo.state,
                        type = incidentInfo.type,
                        images = encodedImages,
                        dates = publicationDates,
                        location = incidentInfo.location,
                )

        return incidentRepository.save(newIncident)
    }

    // fun updateIncident(updateRequest : UpdateIncident, images: List<MultipartFile>) : Incident {

    //     val encodedImages : List<String> = listOfEncodedImages(images)

    // }

    private fun listOfEncodedImages(images: List<MultipartFile>) = images.map { encodeImageAsBase64(it)}

    private fun encodeImageAsBase64(image: MultipartFile): String =
            Base64.getEncoder().encodeToString(image.bytes)


}
