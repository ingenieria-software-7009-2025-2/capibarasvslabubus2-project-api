package com.unam.fciencias.urbanincidents.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class INCIDENT_STATE(val state: String) {
    REPORTED("reported"),
    IN_PROGRESS("inProgress"),
    SOLVED("solved");

    val stateLowerCase: String
        get() = state.lowercase()

    @JsonValue fun toValue(): String = state

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): INCIDENT_STATE {
            return entries.firstOrNull { it.state.equals(value, ignoreCase = true) }
                    ?: throw IllegalArgumentException("No enum constant for value $value")
        }
    }

    fun isBefore(other: INCIDENT_STATE): Boolean = this.ordinal < other.ordinal

    fun isLessOrEqual(other: INCIDENT_STATE): Boolean = this.ordinal <= other.ordinal

    fun isAfter(other: INCIDENT_STATE): Boolean = this.ordinal > other.ordinal

    fun nextOrSame(): INCIDENT_STATE = entries.getOrNull(ordinal + 1) ?: this

    fun next(): INCIDENT_STATE? = entries.getOrNull(ordinal + 1)

    fun previous(): INCIDENT_STATE? = entries.getOrNull(ordinal - 1)

    fun previousOrSame(): INCIDENT_STATE = entries.getOrNull(ordinal - 1) ?: this
}
