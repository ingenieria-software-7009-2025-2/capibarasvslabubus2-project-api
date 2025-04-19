package com.unam.fciencias.urbanincidents.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class USER_ROLE(private val role: String) {
    USER("user"),
    ADMIN("admin"); 

    @JsonValue
    fun toValue(): String = role

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): USER_ROLE {
            return entries.firstOrNull {
                it.role.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("No enum constant for value $value")
        }
    }
}
