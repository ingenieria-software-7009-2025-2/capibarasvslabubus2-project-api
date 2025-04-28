package com.unam.fciencias.urbanincidents.incident.service

import com.unam.fciencias.urbanincidents.enums.*
import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.model.*
import com.unam.fciencias.urbanincidents.incident.repository.IncidentRepository
import com.unam.fciencias.urbanincidents.user.model.*
import com.unam.fciencias.urbanincidents.user.service.UserService
import java.time.LocalDate
import java.util.Base64
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * Service class responsible for managing incidents and applying business logic. Handles operations
 * such as creating, updating, validating, and retrieving incidents.
 */
@Service
class IncidentService(
        private val incidentRepository: IncidentRepository,
        private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves an incident by its ID.
     *
     * @param id The ID of the incident to fetch.
     * @return The found [Incident].
     * @throws IncidentNotFoundException If the incident is not found.
     */
    fun getIncidentById(id: String): Incident =
            incidentRepository.findById(id).orElseThrow { IncidentNotFoundException() }

    /**
     * Checks if an incident with the given ID exists in the repository.
     *
     * @param id The ID of the incident to check.
     * @return True if the incident exists, false otherwise.
     */
    fun existsIncidentById(id: String): Boolean = incidentRepository.existsById(id)

    /**
     * Returns a list with the incidents that match the values in the given list
     *
     * @param types The list with the types that an incident mush have.
     * @param states The list with the states that the incident must have.
     * @param archived The archived value that the incident must have.
     * @return The incidents that match the past criteria.
     */
    fun getFilterIncidents(
            types: List<INCIDENT_TYPE>,
            states: List<INCIDENT_STATE>,
            archived: Boolean
    ): List<Incident> = incidentRepository.getFilterIncidents(types, states, archived)

    /**
     * Creates a new incident with an initial "reported" state.
     *
     * @param incidentInfo Metadata and details for the incident.
     * @param images List of image files to attach.
     * @return The created [Incident].
     * @throws UserNotFoundException If the incident owner doesn't exist.
     * @throws InvalidImagesListException If the images list is null or empty.
     */
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

        val reportedState = SpecificState(LocalDate.now(), listOfEncodedImages(images))
        val states = States(reported = reportedState, inProgress = null, solved = null)

        val newIncident: Incident =
                incidentRepository.save(
                        Incident(
                                id = null,
                                ownerId = incidentInfo.ownerId,
                                states = states,
                                type = incidentInfo.type,
                                description = incidentInfo.description,
                                location = incidentInfo.location,
                                archived = false
                        )
                )

        val newIncidentId: String =
                newIncident.id
                        ?: throw throw UrbanIncidentsException("Incident ID should not be null")

        userService.addIncidentToUserList(newIncident.ownerId, newIncidentId)
        return newIncident
    }

    /**
     * Updates an existing incident's state or other properties.
     *
     * @param updateRequest The update request with optional fields.
     * @param images Optional new images for the given state.
     * @return The updated [Incident].
     */
    fun patchIncident(updateRequest: PatchIncidentRequest, images: List<MultipartFile>?): Incident {
        val incident = getIncidentById(updateRequest.id)
        val userRole = userService.getUserRoleByToken(updateRequest.userToken)

        isValidUpdate(
                incident,
                updateRequest.state,
                images,
                updateRequest.date,
                updateRequest.description,
                updateRequest.type,
                updateRequest.location,
                updateRequest.archived,
                userRole
        )

        images?.let {
            incidentRepository.updateStateImagesById(
                    updateRequest.id,
                    listOfEncodedImages(it),
                    updateRequest.state
            )
        }

        updateRequest.date?.let {
            incidentRepository.updateStateDateById(updateRequest.id, it, updateRequest.state)
        }

        updateRequest.description?.let {
            incidentRepository.updatedDescriptionById(updateRequest.id, it)
        }

        updateRequest.type?.let { incidentRepository.updateTypeById(updateRequest.id, it) }

        updateRequest.location?.let { incidentRepository.updateLocationById(updateRequest.id, it) }

        return getIncidentById(updateRequest.id)
    }

    /**
     * Delete an incident by id. This operations is only valid if the given user is an admin user or
     * if the user is trying to deleting its own incident.
     *
     * @param token The token of the user trying to delete.
     * @param id The id of the incident to delete.
     */
    fun deleteIncident(token: String, id: String) {
        val user: User = userService.getUserByToken(token)

        if (!existsIncidentById(id)) {
            throw IncidentNotFoundException()
        }

        if (user.role != USER_ROLE.ADMIN && (user.incidents == null || !user.incidents.contains(id))
        ) {
            throw UnauthorizedIncidentException()
        }

        val ownerID: String = getOwnerIdByIncidentId(id)
        incidentRepository.deleteById(id)
        userService.removeIncidentFromUserList(ownerID, id)
    }

    fun getOwnerIdByIncidentId(id: String): String =
            incidentRepository.incidentOwnerByIncidentId(id)
                    ?: throw InvalidIncidentIdException(
                            "The incident with id: $id does not have an owner"
                    )

    /**
     * Validates an incident update request before applying any changes.
     *
     * This method checks:
     * - Whether critical fields are being modified without providing images.
     * - Whether the state transition is allowed based on the current incident state.
     * - Whether the provided date is consistent with surrounding state dates.
     * - Whether the user has the appropriate role to modify the 'archived' status.
     *
     * @param incident The existing incident to be updated.
     * @param newState The target state for the incident update.
     * @param images A list of image files associated with the update (may be null).
     * @param newDate New date for the given state (optional).
     * @param newDescription New description for the incident (optional).
     * @param newType New type of incident (optional).
     * @param newLocation New geographical location of the incident (optional).
     * @param newArchivedState Whether the 'archived' field is to be modified (optional).
     * @param userRole The role of the user making the request.
     *
     * @throws InvalidImagesListException If critical fields are modified without images.
     * @throws InvalidIncidentStateException If the transition to the new state is invalid.
     * @throws InvalidIncidentDateException If the new date is inconsistent.
     * @throws InvalidIncidentUpdateException If a non-admin attempts to change the 'archived'
     * status.
     */
    private fun isValidUpdate(
            incident: Incident,
            newState: INCIDENT_STATE,
            images: List<MultipartFile>?,
            newDate: LocalDate?,
            newDescription: String?,
            newType: INCIDENT_TYPE?,
            newLocation: GeoJsonPoint?,
            newArchivedState: Boolean?,
            userRole: USER_ROLE
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

    /**
     * Validates whether the user has permission to modify the 'archived' status of an incident.
     *
     * Only users with the ADMIN role are allowed to change the 'archived' field.
     *
     * @param modifyArchivedValue Indicates whether the archived field is being modified.
     * @param userRole The role of the user attempting the update.
     *
     * @throws InvalidIncidentUpdateException If a non-admin user tries to change the archived
     * status.
     */
    private fun isNewArchivedStateValid(modifyArchivedValue: Boolean, userRole: USER_ROLE) {
        if (modifyArchivedValue && userRole != USER_ROLE.ADMIN) {
            throw InvalidIncidentUpdateException(
                    "You can't modify the archived value of a publication if you are not an admin user"
            )
        }
    }

    /**
     * Validates that a new state date is chronologically correct.
     *
     * Ensures that the new date is:
     * - Not earlier than the previous state's date (if any).
     * - Not later than the next state's date (if any).
     *
     * This maintains a consistent and logical progression of state transitions.
     *
     * @param incident The incident whose states are being evaluated.
     * @param newDate The proposed new date for the state.
     * @param newState The state for which the date is being changed.
     *
     * @throws InvalidIncidentDateException If the new date is out of valid bounds.
     */
    private fun isNewDateValid(incident: Incident, newDate: LocalDate?, newState: INCIDENT_STATE) {
        if (newDate == null) return

        val previousState = newState.previous()
        val nextState = newState.next()

        val previousDate = getStateDate(incident, previousState)
        val nextDate = getStateDate(incident, nextState)

        if (previousDate != null && newDate.isBefore(previousDate)) {
            throw InvalidIncidentDateException(
                    "The new date $newDate for the state ${newState.stateLowerCase} is invalid because it is before the date $previousDate for the state ${previousState?.stateLowerCase}"
            )
        }

        if (nextDate != null && newDate.isAfter(nextDate)) {
            throw InvalidIncidentDateException(
                    "The new date $newDate for the state ${newState.stateLowerCase} is invalid because it is after the date $nextDate for the state ${nextState?.stateLowerCase}"
            )
        }
    }

    /**
     * Retrieves the date associated with a specific state in an incident.
     *
     * @param incident The incident from which the date is retrieved.
     * @param state The state whose date is requested.
     * @return The date of the specified state, or null if not defined.
     */
    private fun getStateDate(incident: Incident, state: INCIDENT_STATE?): LocalDate? =
            when (state) {
                null -> null
                INCIDENT_STATE.REPORTED -> incident.states.reported.date
                INCIDENT_STATE.IN_PROGRESS -> incident.states.inProgress?.date
                INCIDENT_STATE.SOLVED -> incident.states.solved?.date
            }

    /**
     * Validates that a state transition is allowed based on the current incident states.
     *
     * Ensures that the previous required state exists before moving to the new one. For example, an
     * incident cannot move to IN_PROGRESS if it has not been REPORTED.
     *
     * @param incident The current incident.
     * @param newState The desired new state.
     *
     * @throws InvalidIncidentStateException If the required previous state is not defined.
     */
    private fun isNewStateValid(incident: Incident, newState: INCIDENT_STATE) {
        val previousState = newState.previousOrSame()
        if (!checkIfStateExist(incident, previousState)) {
            throw InvalidIncidentStateException(
                    "You can't update state ${newState.stateLowerCase} because state ${previousState.stateLowerCase} is not defined"
            )
        }
    }

    /**
     * Checks if a specific state has already been defined in the incident.
     *
     * @param incident The incident to check.
     * @param state The state to verify.
     * @return True if the state is defined, false otherwise.
     */
    private fun checkIfStateExist(incident: Incident, state: INCIDENT_STATE?): Boolean =
            when (state) {
                INCIDENT_STATE.REPORTED -> true
                INCIDENT_STATE.IN_PROGRESS -> incident.states.inProgress != null
                INCIDENT_STATE.SOLVED -> incident.states.solved != null
                else -> false
            }

    /**
     * Ensures that certain fields (date, description, type, or location) are only updated when
     * images are provided.
     *
     * If images are missing but any of the specified fields are being modified, the update is
     * rejected.
     *
     * @param images List of images provided with the update request.
     * @param modifyDate Whether the date field is being modified.
     * @param modifyDescription Whether the description field is being modified.
     * @param modifyType Whether the type field is being modified.
     * @param modifyLocation Whether the location field is being modified.
     *
     * @throws InvalidImagesListException If modifications are attempted without required images.
     */
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

    /**
     * Converts a list of image files into Base64-encoded strings.
     *
     * This is used to store image data in a text-based format.
     *
     * @param images The list of image files to encode.
     * @return A list of Base64-encoded image strings.
     */
    private fun listOfEncodedImages(images: List<MultipartFile>): List<String> =
            images.map { encodeImageAsBase64(it) }

    /**
     * Converts a single image file into a Base64-encoded string.
     *
     * @param image The image file to encode.
     * @return The Base64-encoded representation of the image.
     */
    private fun encodeImageAsBase64(image: MultipartFile): String =
            Base64.getEncoder().encodeToString(image.bytes)
}
