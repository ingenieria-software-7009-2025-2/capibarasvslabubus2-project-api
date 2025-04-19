package com.unam.fciencias.urbanincidents.incident.service

import com.unam.fciencias.urbanincidents.enums.*
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

    fun getIncidentById(id: String): Incident =
            incidentRepository.findById(id).orElseThrow { IncidentNotFoundException() }

    fun createIncident(incidentInfo: CreateIncident, images: List<MultipartFile>): Incident {
        if (!userService.existsUserById(incidentInfo.ownerId)) {
            throw UserNotFoundException()
        }

        val reportedState: SpecificState =
                SpecificState(date = LocalDate.now(), images = listOfEncodedImages(images))
        val incidentStates: States =
                States(
                        reported = reportedState,
                        inProgress = null,
                        solved = null,
                )

        val newIncident: Incident =
                Incident(
                        id = null,
                        ownerId = incidentInfo.ownerId,
                        states = incidentStates,
                        type = incidentInfo.type,
                        description = incidentInfo.description,
                        location = incidentInfo.location,
                )

        return incidentRepository.save(newIncident)
    }

    fun updateIncident(updateRequest: UpdateIncident, images: List<MultipartFile>?): Incident {

        val incident: Incident = getIncidentById(updateRequest.id)

        isValidUpdate(incident, updateRequest.date, updateRequest.state)

        val encodedImages: List<String> = listOfEncodedImages(images)

        if (encodedImages.isNotEmpty()) {
            incidentRepository.updateStateImagesById(
                    updateRequest.id,
                    encodedImages,
                    updateRequest.state
            )
        } else {
            if (updateRequest.date != null) {
                throw InvalidIncidentUpdateException(
                        "You can't update an incident state without providing evidence (images)"
                )
            }
        }

        updateRequest.date?.let {
            incidentRepository.updateStateDateById(
                    updateRequest.id,
                    updateRequest.date,
                    updateRequest.state
            )
        }

        updateRequest.description?.let {
            incidentRepository.updatedDescriptionById(updateRequest.id, updateRequest.description)
        }

        updateRequest.type?.let {
            incidentRepository.updateTypeById(updateRequest.id, updateRequest.type)
        }

        updateRequest.location?.let {
            incidentRepository.updateLocationById(updateRequest.id, updateRequest.location)
        }

        return getIncidentById(updateRequest.id)
    }

    private fun isValidUpdate(incident: Incident, newDate: LocalDate?, newState: INCIDENT_STATE) {
        isNewStateValid(incident, newState)
        isNewDateValid(incident, newDate, newState)
    }

    private fun isNewDateValid(incident: Incident, newDate: LocalDate?, newState: INCIDENT_STATE) {
        if (newDate == null) {
            return
        }

        val previosState: INCIDENT_STATE? = newState.previous()
        val nextState: INCIDENT_STATE? = newState.next()

        val previousDate: LocalDate? = getStateDate(incident, previosState)
        val nextDate: LocalDate? = getStateDate(incident, nextState)

        if (previousDate != null && newDate.isBefore(previousDate)) {
            throw InvalidIncidentDateException(
                    "The new date $newDate for the state ${newState.stateLowerCase} is invalid because is less than the date $previousDate for the state ${previosState?.stateLowerCase}"
            )
        }

        if (nextDate != null && newDate.isAfter(nextDate)) {
            throw InvalidIncidentDateException(
                    "The new date $newDate for the state ${newState.stateLowerCase} is invalid because is greater than the date $nextDate for the state ${nextState?.stateLowerCase}"
            )
        }
    }

    private fun getStateDate(incident: Incident, state: INCIDENT_STATE?): LocalDate? =
            when (state) {
                null -> null
                INCIDENT_STATE.REPORTED -> incident.states.reported.date
                INCIDENT_STATE.IN_PROGRESS -> incident.states.inProgress?.date
                INCIDENT_STATE.SOLVED -> incident.states.solved?.date
            }

    private fun isNewStateValid(incident: Incident, newState: INCIDENT_STATE) {
        val previousState: INCIDENT_STATE = newState.previousOrSame()
        if (!checkIfStateExist(incident, previousState)) {
            throw InvalidIncidentStateException(
                    "You can't update state ${newState.stateLowerCase} because there is not state ${previousState.stateLowerCase} defined"
            )
        }
    }

    private fun checkIfStateExist(incident: Incident, state: INCIDENT_STATE?): Boolean =
            when (state) {
                INCIDENT_STATE.REPORTED -> true
                INCIDENT_STATE.IN_PROGRESS -> incident.states.inProgress != null
                INCIDENT_STATE.SOLVED -> incident.states.solved != null
                else -> false
            }

    private fun lastValidState(incident: Incident): INCIDENT_STATE {
        incident.states.solved?.let {
            return INCIDENT_STATE.SOLVED
        }

        incident.states.inProgress?.let {
            return INCIDENT_STATE.IN_PROGRESS
        }

        return INCIDENT_STATE.REPORTED
    }

    private fun listOfEncodedImages(images: List<MultipartFile>?): List<String> =
            images?.map { encodeImageAsBase64(it) } ?: emptyList()

    private fun encodeImageAsBase64(image: MultipartFile): String =
            Base64.getEncoder().encodeToString(image.bytes)

    fun existsIncidentById(id: String): Boolean = incidentRepository.findById(id).isPresent()
}
