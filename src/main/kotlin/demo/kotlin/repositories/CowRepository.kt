package demo.kotlin.repositories

import demo.kotlin.entities.Cow
import org.springframework.stereotype.Repository
import org.ufoss.kolog.Logger
import org.ufoss.kotysa.spring.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.time.LocalDate
import java.util.*

private val logger = Logger.of<CowRepository>()

@Repository
class CowRepository(override val sqlClient: ReactorSqlClient) : Repo<Cow, COW> {

    override val table = COW

    fun findByName(name: String) =
            (sqlClient selectFrom COW
                    where COW.name eq name
                    ).fetchFirst()

    override fun init() =
            arrayOf(
                    Cow("Marguerite", LocalDate.of(2017, 9, 28), id = COW_MARGUERITE_UUID),
                    Cow("La Noiraude")
            )
                    .toFlux()
                    .doOnNext { cow -> logger.info { "saving cow $cow" } }
                    .flatMap { cow -> save(cow) }
                    .then()
}

internal val COW_MARGUERITE_UUID = UUID.fromString("e48ccc7e-c1b8-41b8-91f5-ab5528ab292b")
