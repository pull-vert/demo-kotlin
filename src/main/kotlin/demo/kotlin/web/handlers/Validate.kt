package demo.kotlin.web.handlers

import demo.kotlin.web.BadRequestStatusException
import javax.validation.Validator

interface Validate {
    val validator: Validator

    fun callValidator(any: Any) {
        val errors = validator.validate(any)
        if (errors.isNotEmpty()) {
            var msg = ""
            errors.forEach { error ->
                msg += "${error.propertyPath}(${error.invalidValue}):${error.message};"
            }
            msg = msg.removeSuffix(";") // remove last ';'
            throw BadRequestStatusException(msg)
        }
    }
}
