package com.unam.fciencias.urbanincidents.validator

import jakarta.validation.*
import kotlin.reflect.KClass

enum class ROLE {
  USER, ADMIN,
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [ValidRoleValidator::class])
annotation class ValidRole(
    val message: String = "This role is not defined", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidRoleValidator : EnumValidator<ValidRole, ROLE>(ROLE::class.java)


