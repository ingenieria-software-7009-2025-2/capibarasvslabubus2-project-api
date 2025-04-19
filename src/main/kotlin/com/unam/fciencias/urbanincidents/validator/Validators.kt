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

abstract class EnumValidator<A : Annotation, E : Enum<E>>(
    private val enumClass: Class<E>
) : ConstraintValidator<A, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return true

        return try {
            java.lang.Enum.valueOf(enumClass, value.uppercase())
            true
        } catch (e: IllegalArgumentException) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "The value '$value' is invalid. Must be one of the following: ${enumClass.enumConstants.joinToString(", ") { it.name.lowercase() }}"
            ).addConstraintViolation()
            false
        }
    }
}

