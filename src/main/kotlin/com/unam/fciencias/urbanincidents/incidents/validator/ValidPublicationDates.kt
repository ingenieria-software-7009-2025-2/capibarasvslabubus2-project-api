package com.unam.fciencias.urbanincidents.validator

import jakarta.validation.*
import kotlin.reflect.KClass
import com.unam.fciencias.urbanincidents.incident.model.PublicationDates

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [ValidPublicationDatesValidator::class])
annotation class ValidPublicationDates(
    val message: String = "This publication dates are invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidPublicationDatesValidator : ConstraintValidator<ValidPublicationDates, PublicationDates> {

    override fun isValid(value: PublicationDates, context: ConstraintValidatorContext): Boolean {

        value.inProgress?.let {
            if (!it.isAfter(value.reported)) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "inProgress date must be after reported date"
                ).addConstraintViolation()
                return false
            }
        }

        value.resolution?.let {
            if (value.inProgress == null) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "You cannot set a resolution date if there is no inProgress date"
                ).addConstraintViolation()
                return false
            }

            if (!it.isAfter(value.inProgress)) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "resolution date must be after inProgress date"
                ).addConstraintViolation()
                return false
            }
        }

        return true
    }
}
