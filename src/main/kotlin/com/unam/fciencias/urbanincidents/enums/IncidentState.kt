package com.unam.fciencias.urbanincidents.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class INCIDENT_STATE(private val state: String) {
    REPORTED("reported"),
    IN_PROGRESS("in progress"),
    SOLVED("solved");

    @JsonValue
    fun toValue(): String = state

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): INCIDENT_STATE {
            return entries.firstOrNull {
                it.state.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("No enum constant for value $value")
        }
    }

    fun isBefore(other: INCIDENT_STATE): Boolean {
        return this.ordinal < other.ordinal
    }

    fun isAfter(other: INCIDENT_STATE): Boolean {
        return this.ordinal > other.ordinal
    }

    fun next(): INCIDENT_STATE {
        return entries.getOrNull(ordinal + 1) ?: this
    }

    fun previous(): INCIDENT_STATE {
        return entries.getOrNull(ordinal - 1) ?: this
    }
}
