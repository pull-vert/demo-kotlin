package demo.kotlin.web

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal class BadRequestStatusException(reason: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)
internal class UnauthorizedStatusException : ResponseStatusException(HttpStatus.UNAUTHORIZED)
internal class NotFoundStatusException : ResponseStatusException(HttpStatus.NOT_FOUND)
