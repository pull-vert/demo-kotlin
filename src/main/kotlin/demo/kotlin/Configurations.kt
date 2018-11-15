package demo.kotlin

import demo.kotlin.repository.CowRepository
import demo.kotlin.web.CowHandler
import demo.kotlin.web.routes
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.mongo.embedded
import org.springframework.fu.kofu.mongo.mongodb
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.server

internal val dataConfig = configuration {
    beans {
        bean<CowRepository>()
        bean<DatabaseInitializer>()
    }
    listener<ApplicationReadyEvent> {
        ref<DatabaseInitializer>().init()
    }
    mongodb {
        embedded()
    }
}

internal val webConfig = configuration {
    beans {
        bean<CowHandler>()
    }
    server {
        port = if (profiles.contains("test")) 8181 else 8080
        codecs {
            jackson()
        }
        import(::routes)
    }
}
