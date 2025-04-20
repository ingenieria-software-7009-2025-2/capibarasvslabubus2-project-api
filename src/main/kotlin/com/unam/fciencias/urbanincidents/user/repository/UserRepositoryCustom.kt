package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.enums.USER_ROLE
import com.unam.fciencias.urbanincidents.user.model.Name

interface UserRepositoryCustom {
    fun updateEmailById(id: String, email: String)
    fun updatePasswordById(id: String, password: String)
    fun updateTokenById(id: String, token: String)
    fun updateNameById(id: String, name: Name)
    fun patchIncidentsById(id: String, incidents: List<String>)
    fun findUserRoleByToken(token: String): USER_ROLE?
}
