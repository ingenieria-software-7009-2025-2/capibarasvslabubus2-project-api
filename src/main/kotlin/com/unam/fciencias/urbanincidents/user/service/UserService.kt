package com.unam.fciencias.urbanincidents.user.service

import com.unam.fciencias.urbanincidents.enums.USER_ROLE
import com.unam.fciencias.urbanincidents.exception.*
import com.unam.fciencias.urbanincidents.incident.service.IncidentService
import com.unam.fciencias.urbanincidents.user.model.CreaterUserRequest
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.PatchUserRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.repository.UserRepository
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

/**
 * Service responsible for managing users, including creation, authentication, updates, and
 * interaction with related incidents.
 */
@Service
class UserService(
        private val userRepository: UserRepository,
        @Lazy private val incidentService: IncidentService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Retrieves a user by their ID.
     *
     * @param id The user's ID.
     * @return The user associated with the ID.
     * @throws UserNotFoundException If the user is not found.
     */
    fun getUserById(id: String): User =
            userRepository.findById(id).orElseThrow { UserNotFoundException() }

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    fun getUsers(): List<User> = userRepository.findAll()

    /**
     * Retrieves a user by their token.
     *
     * @param token The user's token.
     * @return The corresponding user.
     * @throws InvalidTokenException If the token is invalid or expired.
     */
    fun getUserByToken(token: String): User =
            userRepository.findByToken(token)
                    ?: throw InvalidTokenException(
                            InvalidTokenException.generateMessageWithToken(token)
                    )

    /**
     * Retrieves a user by their email.
     *
     * @param email The email to search for.
     * @return The user with the given email.
     * @throws EmailNotFoundException If no user is found with that email.
     */
    fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email)
                    ?: throw EmailNotFoundException(
                            EmailNotFoundException.generateMessageWithEmail(email)
                    )

    /**
     * Retrieves the role of a user by their token.
     *
     * @param token The user's token.
     * @return The role of the user.
     * @throws InvalidTokenException If the token is invalid or expired.
     */
    fun getUserRoleByToken(token: String): USER_ROLE =
            userRepository.findUserRoleByToken(token) ?: throw InvalidTokenException()

    /**
     * Checks if a user exists by their ID.
     *
     * @param id The user ID.
     * @return True if the user exists, false otherwise.
     */
    fun existsUserById(id: String): Boolean = userRepository.existsById(id)

    /**
     * Retrieves the user id by his token.
     *
     * @param token The token of the user.
     * @thros InvalidTokenException if the token does not correspond to a user
     */
    fun getUserIdByToken(token: String): String =
            userRepository.findUserIdByToken(token) ?: throw InvalidTokenException()

    /**
     * Creates a new user.
     *
     * @param request The data required to create the user.
     * @return The created user.
     * @throws UserAlreadyExistsException If a user with the given email already exists.
     */
    fun createUser(request: CreaterUserRequest): User {
        userRepository.findByEmail(request.email)?.let {
            throw UserAlreadyExistsException(
                    UserAlreadyExistsException.generateMessageWithEmail(request.email)
            )
        }

        val user =
                User(
                        id = null,
                        role = USER_ROLE.USER,
                        email = request.email,
                        password = request.password,
                        token = "",
                        incidents = emptyList<String>()
                )
        return userRepository.save(user)
    }

    /**
     * Logs in a user by validating their email and password.
     *
     * @param loginRequest The login credentials.
     * @return The authenticated user with a new token.
     * @throws UserNotFoundException If the user is not found.
     */
    fun loginUser(loginRequest: LoginRequest): User? {
        val user =
                userRepository.findByEmailAndPassword(loginRequest.email, loginRequest.password)
                        ?: throw UserNotFoundException(
                                "The user was not found with the given email and password"
                        )

        val userId = user.id ?: throw UrbanIncidentsException("User ID should not be null")
        val token = UUID.randomUUID().toString()
        userRepository.updateTokenById(userId, token)
        return user.copy(token = token)
    }

    /**
     * Logs out a user by clearing their token.
     *
     * @param token The user's current token.
     * @throws InvalidTokenException If the token is invalid.
     */
    fun logoutUser(token: String) {
        val userFound =
                userRepository.findByToken(token)
                        ?: throw InvalidTokenException(
                                InvalidTokenException.generateMessageWithToken(token)
                        )

        userRepository.updateTokenById(userFound.id.toString(), "")
    }

    /**
     * Partially updates the user's fields using the provided token.
     *
     * @param token The user's authentication token.
     * @param updateRequest The fields to be updated.
     * @return The updated user.
     * @throws InvalidTokenException If the token is invalid.
     */
    fun patchUserByToken(token: String, updateRequest: PatchUserRequest): User {
        val user =
                userRepository.findByToken(token)
                        ?: throw InvalidTokenException(
                                InvalidTokenException.generateMessageWithToken(token)
                        )

        val userId = user.id ?: throw UrbanIncidentsException("User ID should not be null")

        if (updateRequest.email != null && updateRequest.email.isEmpty()) {
            throw InvalidUserUpdateException("The email can't be empty")
        }

        if (updateRequest.password != null && updateRequest.password.isEmpty()) {
            throw InvalidUserUpdateException("The password can't be empty")
        }

        isValidUpdate(userId, updateRequest.email)
        updateRequest.email?.let { userRepository.updateEmailById(userId, it) }
        updateRequest.password?.let { userRepository.updatePasswordById(userId, it) }

        return getUserById(userId)
    }

    /**
     * Delete an user by id. This operations is only valid if the given user is an admin user or if
     * the user is trying to deleting its own account.
     *
     * @param token The token of the user trying to delete.
     * @param id The id of the user to delete.
     */
    fun deleteUserById(token: String, id: String) {
        val userByToken: User = getUserByToken(token)
        val userToDelete: User = getUserById(id)

        if (userByToken.role != USER_ROLE.ADMIN && userByToken.id != id) {
            throw UnauthorizedUserException()
        }

        if (userToDelete.incidents != null) {
            for (incidentId in userToDelete.incidents) {
                incidentService.deleteIncident(token, incidentId)
            }
        }
        userRepository.deleteById(id)
    }

    /**
     * Removes a specific incident id from the list of incidnets of the user
     *
     * @param userId The user id whose list we are going to remove from.
     * @param incidentId The incident id we are going to look to remove.
     */
    fun removeIncidentFromUserList(userId: String, incidentId: String) =
            userRepository.removeIncidentFromUserList(userId, incidentId)

    /**
     * Adds a incident id to the users list.
     *
     * @param userId The user id wo witch we are going to add the incident id.
     * @param incidentId The incident id to add to the user's list.
     */
    fun addIncidentToUserList(userId: String, incidentId: String) =
            userRepository.pushIncidentToUserList(userId, incidentId)

    /**
     * Validates update input fields such as email uniqueness and incident IDs.
     *
     * @param userId The ID of the user to update.
     * @param email The new email (optional).
     * @throws UserAlreadyExistsException If the email is already in use.
     * @throws InvalidIncidentIdException If incident IDs are invalid or duplicated.
     */
    fun isValidUpdate(userId: String, email: String?) {
        email?.let {
            val existingUser = userRepository.findByEmail(it)
            if (existingUser != null && existingUser.id != userId) {
                throw UserAlreadyExistsException(
                        UserAlreadyExistsException.generateMessageWithEmail(it)
                )
            }
        }
    }
}
