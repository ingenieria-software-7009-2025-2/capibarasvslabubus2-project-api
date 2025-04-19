package com.unam.fciencias.urbanincidents.validator

import com.unam.fciencias.urbanincidents.enum.INCIDENT_TYPE
import jakarta.validation.*
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [ValidTypeValidator::class])
annotation class ValidType(
    val message: String = "This type is not defined", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidTypeValidator : EnumValidator<ValidType, INCIDENT_TYPE>(INCIDENT_TYPE::class.java)





