package com.unam.fciencias.urbanincidents.user.repository

import com.unam.fciencias.urbanincidents.user.model.User
import com.unam.fciencias.urbanincidents.user.model.Name

interface UserRepositoryCustom {
    fun updateEmailById(id: String, email: String)
    fun updatePasswordById(id: String, password: String)
    fun updateTokenById(id: String, token: String)
    fun updateNameById(id: String, name: Name)
}
