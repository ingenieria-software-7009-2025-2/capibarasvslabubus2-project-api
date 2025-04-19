package com.unam.fciencias.urbanincidents.user.service

import com.unam.fciencias.urbanincidents.exception.EmailNotFoundException
import com.unam.fciencias.urbanincidents.exception.InvalidTokenException
import com.unam.fciencias.urbanincidents.exception.UserAlreadyExistsException
import com.unam.fciencias.urbanincidents.exception.UserNotFoundException
import com.unam.fciencias.urbanincidents.exception.UrbanIncidentsException
import com.unam.fciencias.urbanincidents.user.model.CreateUser
import com.unam.fciencias.urbanincidents.user.model.LoginRequest
import com.unam.fciencias.urbanincidents.user.model.UpdateUserRequest
import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import javax.management.relation.InvalidRelationIdException

@Service
class UserService(private val userRepository: UserRepository) {

    /**
     * Creates a new user in the database.
     *
     * @param request The request object containing the user's email and password.
     * @return The saved user with a generated ID.
     */
    fun createUser(request: CreateUser): User {
        userRepository.findByEmail(request.email)?.let {
            throw UserAlreadyExistsException(UserAlreadyExistsException.generateMessageWithEmail(request.email))
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
     * This method finds a user by email and password, then updates their token to login.
     * @param loginRequest with the fields of mail and password.
     * @return the user updated if is found, otherwise returns null.
     */
    fun loginUser(loginRequest: LoginRequest): User? {
        // Search user
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

    /**
     * This method finds a user by token, then invalidates the token to logout.
     * @param token the token of the user that is closing session.
     * @return true if the user was found.
     */
    fun logoutUser(token: String): Unit {
        val userFound = userRepository.findByToken(token) ?: throw InvalidTokenException(InvalidTokenException.generateMessageWithToken(token))
        userRepository.updateTokenById(userFound.id.toString(), "")
    }

    fun updateUserByToken(token: String, updateRequest: UpdateUserRequest): User {
        val user = userRepository.findByToken(token) ?: throw InvalidTokenException(InvalidTokenException.generateMessageWithToken(token))

        val userId = user.id ?: throw UrbanIncidentsException("User ID should not be null")

        if (updateRequest.email == null &&
                        updateRequest.password == null &&
                        updateRequest.name == null
        ){
                return user
        }

        updateRequest.email?.let { newEmail ->
            val existingUser = userRepository.findByEmail(newEmail)
            if (existingUser != null && existingUser.id != userId) {
                throw UserAlreadyExistsException(UserAlreadyExistsException.generateMessageWithEmail(newEmail))
            }
            userRepository.updateEmailById(userId, newEmail)
        }

        updateRequest.password?.let { userRepository.updatePasswordById(userId, it) }

        updateRequest.name?.let { userRepository.updateNameById(userId, it) }

        return userRepository.findById(userId).orElseThrow { UserNotFoundException() }
    }

    fun getUserByToken(token: String): User =
            userRepository.findByToken(token) ?: throw InvalidTokenException(InvalidTokenException.generateMessageWithToken(token))

    fun getUserById(id: String): User =
            userRepository.findById(id).orElseThrow { UserNotFoundException() }

    fun getUserByEmail(email: String): User =
            userRepository.findByEmail(email) ?: throw EmailNotFoundException(EmailNotFoundException.generateMessageWithEmail(email))

    fun existsUserById(id: String): Boolean = userRepository.findById(id).isPresent()
}
