package com.unam.fciencias.urbanincidents.incident.service

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.repository.IncidentRepository
import com.unam.fciencias.urbanincidents.user.model.*
import com.unam.fciencias.urbanincidents.user.service.UserService
import java.time.LocalDate
import java.util.Base64
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class IncidentService(
        private val incidentRepository: IncidentRepository,
        private val userService: UserService
) {

    fun getIncidentById(id: String): Incident =
            incidentRepository.findById(id).orElseThrow { IncidentNotFoundException() }

    fun createIncident(
            incidentInfo: CreateIncidentRequest,
            images: List<MultipartFile>?
    ): Incident {
        if (!userService.existsUserById(incidentInfo.ownerId)) {
            throw UserNotFoundException()
        }

        if (images == null || images.isEmpty()) {
            throw InvalidImagesListException("The images list is null or empty")
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
                        archived = false,
                )

        return incidentRepository.save(newIncident)
    }

    fun patchIncident(updateRequest: PatchIncidentRequest, images: List<MultipartFile>?): Incident {

        val incident: Incident = getIncidentById(updateRequest.id)
        val userRole: USER_ROLE = userService.getUserRoleByToken(updateRequest.userToken)

        isValidUpdate(
                incident,
                updateRequest.state,
                images,
                updateRequest.date,
                updateRequest.description,
                updateRequest.type,
                updateRequest.location,
                updateRequest.archived,
                userRole,
        )

        images?.let {
            incidentRepository.updateStateImagesById(
                    updateRequest.id,
                    listOfEncodedImages(images),
                    updateRequest.state
            )
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

    private fun isValidUpdate(
            incident: Incident,
            newState: INCIDENT_STATE,
            images: List<MultipartFile>?,
            newDate: LocalDate?,
            newDescription: String?,
            newType: INCIDENT_TYPE?,
            newLocation: GeoJsonPoint?,
            newArchivedState: Boolean?,
            userRole: USER_ROLE,
    ) {
        isImagesListValid(
                images,
                newDate != null,
                newDescription != null,
                newType != null,
                newLocation != null
        )
        isNewStateValid(incident, newState)
        isNewDateValid(incident, newDate, newState)
        isNewArchivedStateValid(newArchivedState != null, userRole)
    }

    private fun isNewArchivedStateValid(modifyArchivedValue: Boolean, userRole: USER_ROLE) {
        if (modifyArchivedValue && !userRole.equals(USER_ROLE.ADMIN)) {
            throw InvalidIncidentUpdateException(
                    "You can't modify the archived value of a publication if you are not an admin user"
            )
        }
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

    private fun isImagesListValid(
            images: List<MultipartFile>?,
            modifyDate: Boolean,
            modifyDescription: Boolean,
            modifyType: Boolean,
            modifyLocation: Boolean
    ) {
        if (images == null || images.isEmpty()) {
            val modifications = mutableListOf<String>()

            if (modifyDate) modifications += "date"
            if (modifyDescription) modifications += "description"
            if (modifyType) modifications += "type"
            if (modifyLocation) modifications += "location"

            if (modifications.isNotEmpty()) {
                val fields = modifications.joinToString(", ")
                throw InvalidImagesListException(
                        "Cannot modify the following fields without providing images: $fields"
                )
            }
        }

        if (images != null && images.isEmpty()) {
            throw InvalidImagesListException("The images list is empty")
        }
    }

    private fun listOfEncodedImages(images: List<MultipartFile>): List<String> {
        return images.map { encodeImageAsBase64(it) }
    }

    private fun encodeImageAsBase64(image: MultipartFile): String =
            Base64.getEncoder().encodeToString(image.bytes)

    fun existsIncidentById(id: String): Boolean = incidentRepository.findById(id).isPresent()
}
