package demo.kotlin

import org.springframework.fu.kofu.application

internal val app = application {
    import(dataConfig)
    import(webConfig)
}

fun main() {
    app.run()
}
