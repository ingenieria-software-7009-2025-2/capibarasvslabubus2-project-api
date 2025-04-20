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

@Service
class UserService(
        private val userRepository: UserRepository,
        @Lazy private val incidentService: IncidentService
) {

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

    fun loginUser(loginRequest: LoginRequest): User? {
        val user =
                userRepository.findByEmailAndPassword(loginRequest.email, loginRequest.password)
                        ?: throw UserNotFoundException(
                                "The user was not found with the given email and password"
                        )

        val userId = user.id ?: throw UrbanIncidentsException("User ID should not be null")

        val token = UUID.randomUUID().toString()
        userRepository.updateTokenById(userId, token)

        val myUser =
                User(
                        id = userId,
                        name = user.name,
                        role = user.role,
                        email = user.email,
                        token = token,
                        password = user.password,
                        incidents = user.incidents,
                )
        return myUser
    }

    fun logoutUser(token: String): Unit {
        val userFound =
                userRepository.findByToken(token)
                        ?: throw InvalidTokenException(
                                InvalidTokenException.generateMessageWithToken(token)
                        )
        userRepository.updateTokenById(userFound.id.toString(), "")
    }

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

    fun getUserRoleByToken(token: String): USER_ROLE =
            userRepository.findUserRoleByToken(token) ?: throw InvalidTokenException()

    fun getUserByToken(token: String): User =
            userRepository.findByToken(token)
                    ?: throw InvalidTokenException(
                            InvalidTokenException.generateMessageWithToken(token)
                    )

    fun getUserById(id: String): User =
            userRepository.findById(id).orElseThrow { UserNotFoundException() }

    fun getUsers(): List<User> = userRepository.findAll()

    fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email)
                    ?: throw EmailNotFoundException(
                            EmailNotFoundException.generateMessageWithEmail(email)
                    )

    fun existsUserById(id: String): Boolean = userRepository.findById(id).isPresent()
}
