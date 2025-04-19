package com.unam.fciencias.urbanincidents.validator

import jakarta.validation.*
import kotlin.reflect.KClass

enum class TYPE {
  AUTOMOTIVE, ROAD_INFRASTRUCTURE, NATURAL_PHENOMENA, ARCHITECTURE, OTHERS,
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [ValidTypeValidator::class])
annotation class ValidType(
    val message: String = "This type is not defined", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidTypeValidator : EnumValidator<ValidType, TYPE>(TYPE::class.java)





