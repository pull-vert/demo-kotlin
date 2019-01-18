package demo.kotlin.service

import demo.kotlin.web.BadRequestStatusException
import java.util.*

interface IService {
    fun String?.toUuid() =
            try {
                UUID.fromString(this)
            } catch(e: Throwable) {
                throw BadRequestStatusException(e.localizedMessage)
            }
}