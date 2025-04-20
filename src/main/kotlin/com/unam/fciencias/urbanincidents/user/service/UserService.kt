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
    fun existsUserById(id: String): Boolean = userRepository.findById(id).isPresent()

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
                        name = request.name,
                        role = request.role,
                        email = request.email,
                        password = request.password,
                        token = "",
                        incidents = null
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

        isValidUpdate(userId, updateRequest.email, updateRequest.incidents)

        updateRequest.incidents?.let { userRepository.patchIncidentsById(userId, it) }
        updateRequest.email?.let { userRepository.updateEmailById(userId, it) }
        updateRequest.password?.let { userRepository.updatePasswordById(userId, it) }
        updateRequest.name?.let { userRepository.updateNameById(userId, it) }

        return getUserById(userId)
    }

    /**
     * Validates update input fields such as email uniqueness and incident IDs.
     *
     * @param userId The ID of the user to update.
     * @param email The new email (optional).
     * @param incidents The new incidents list (optional).
     * @throws UserAlreadyExistsException If the email is already in use.
     * @throws InvalidIncidentIdException If incident IDs are invalid or duplicated.
     */
    fun isValidUpdate(userId: String, email: String?, incidents: List<String>?) {
        email?.let {
            val existingUser = userRepository.findByEmail(it)
            if (existingUser != null && existingUser.id != userId) {
                throw UserAlreadyExistsException(
                        UserAlreadyExistsException.generateMessageWithEmail(it)
                )
            }
        }

        incidents?.let {
            val incidentsIds = mutableSetOf<String>()
            for (incidentId in it) {
                if (incidentId.isNullOrBlank() || !incidentService.existsIncidentById(incidentId)) {
                    throw InvalidIncidentIdException("Invalid id for an incident in the given list")
                }
                incidentsIds.add(incidentId)
            }
            if (incidentsIds.size != incidents.size) {
                throw InvalidIncidentIdException(
                        "You can't set the same id for a publication twice"
                )
            }
        }
    }
}
