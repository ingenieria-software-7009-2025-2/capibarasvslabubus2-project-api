package com.unam.fciencias.urbanincidents.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class INCIDENT_TYPE(private val type: String) {
    AUTOMOTIVE("automotive"),
    ROAD_INFRASTRUCTURE("road_infrastructure"),
    NATURAL_PHENOMENA("natural_phenomena"),
    ARCHITECTURE("architecture"),
    OTHERS("others");

    val typeToLowerCase: String
        get() = type.lowercase()

    @JsonValue
    fun toValue(): String = type  

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): INCIDENT_TYPE {
            return entries.firstOrNull {
                it.type.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException("No enum constant for value $value")
        }
    }
}
