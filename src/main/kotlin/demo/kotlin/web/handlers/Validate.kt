package demo.kotlin.web.handlers

import demo.kotlin.web.BadRequestStatusException
import javax.validation.Validator

interface Validate {
    val validator: Validator

    fun callValidator(any: Any) {
        val errors = validator.validate(any)
        if (!errors.isEmpty()) {
            var msg = ""
            errors.forEach {
                msg += "${it.propertyPath}(${it.invalidValue}):${it.message};"
            }
            // remove last ;
            msg = msg.removeSuffix(";")
            throw BadRequestStatusException(msg)
        }
    }
}