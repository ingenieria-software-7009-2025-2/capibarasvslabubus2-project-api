package com.unam.fciencias.urbanincidents.validator

import jakarta.validation.*
import kotlin.reflect.KClass

enum class STATE {
  REPORTED, IN_PROGRESS, SOLVED,
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [ValidStateValidator::class])
annotation class ValidState(
    val message: String = "This state is not defined", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidStateValidator : EnumValidator<ValidState, STATE>(STATE::class.java)







