package demo.kotlin.repositories

import demo.kotlin.entities.Cow
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.time.LocalDate
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class CowRepository(override val sqlClient: ReactorSqlClient) : Repo<Cow>() {

    fun findByName(name: String) =
            sqlClient.select<Cow>()
                    .where { it[Cow::name] eq name }
                    .fetchFirst()

    override fun findAll() = findAllReified()

    override fun findById(id: UUID) = findByIdReified(id)

    override fun count() = countReified()

    override fun deleteAll() = deleteAllReified()

    override fun deleteById(id: UUID) = deleteByIdReified(id)

    override fun createTable() = createTableReified()

    override fun init() =
            arrayOf(marguerite, laNoiraude)
                    .toFlux()
                    .doOnNext { cow -> logger.info { "saving cow $cow" } }
                    .flatMap { cow -> save(cow) }
                    .then()
}

internal val COW_MARGUERITE_UUID = UUID.fromString("e48ccc7e-c1b8-41b8-91f5-ab5528ab292b")
internal val marguerite = Cow("Marguerite", LocalDate.of(2017, 9, 28), id = COW_MARGUERITE_UUID)
internal val laNoiraude = Cow("La Noiraude")
