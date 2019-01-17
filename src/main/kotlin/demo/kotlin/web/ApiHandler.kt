package demo.kotlin.web

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

abstract class ApiHandler {
    protected fun String?.toUuid() =
            try {
                UUID.fromString(this)
            } catch(e: Throwable) {
                throw BadRequestStatusException(e.localizedMessage)
            }

    protected class BadRequestStatusException(reason: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)
}