package com.unam.fciencias.urbanincidents.validator

import jakarta.validation.*
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER) 
@Retention(AnnotationRetention.RUNTIME) 
@MustBeDocumented
@Constraint(validatedBy = [NullableNotBlankValidator::class])
annotation class NullableNotBlank(
    val message: String = "This field can't be empty", 
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class NullableNotBlankValidator : ConstraintValidator<NullableNotBlank, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?) = value?.isNotBlank() ?: true
}


