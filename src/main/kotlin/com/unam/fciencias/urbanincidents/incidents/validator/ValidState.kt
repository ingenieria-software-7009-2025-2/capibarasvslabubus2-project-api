package com.unam.fciencias.urbanincidents.validator

import com.unam.fciencias.urbanincidents.enum.INCIDENT_STATE
import jakarta.validation.*
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [ValidStateValidator::class])
annotation class ValidState(
    val message: String = "This state is not defined", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidStateValidator : EnumValidator<ValidState, INCIDENT_STATE>(INCIDENT_STATE::class.java)







