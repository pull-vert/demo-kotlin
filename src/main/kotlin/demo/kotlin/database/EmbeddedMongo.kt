package demo.kotlin.database

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Feature
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.distribution.Versions
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.distribution.GenericVersion
import de.flapdoodle.embed.process.io.Processors
import de.flapdoodle.embed.process.runtime.Network
import org.slf4j.LoggerFactory
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import java.io.IOException
import java.util.*
import java.util.function.Function
import kotlin.collections.LinkedHashMap


/**
 * A MongoDB server resource that is started/stopped along the test lifecycle.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 */
class EmbeddedMongo {

    /**
     * [Builder] for [EmbeddedMongo].
     */
    class Builder internal constructor() {
        var version: IFeatureAwareVersion
        lateinit var replicaSetName: String
        var serverPorts: List<Int>
        var configServerPorts: List<Int>
        var silent = true

        /**
         * Configure the MongoDB [version][IFeatureAwareVersion].
         *
         * @param version
         * @return
         */
        fun withVersion(version: IFeatureAwareVersion): Builder {
            this.version = version
            return this
        }

        /**
         * Configure the replica set name.
         *
         * @param version
         * @return
         */
        fun withReplicaSetName(replicaSetName: String): Builder {
            this.replicaSetName = replicaSetName
            return this
        }

        /**
         * Configure the server ports.
         *
         * @param version
         * @return
         */
        fun withServerPorts(vararg ports: Int): Builder {
            serverPorts = ports.toList()
            return this
        }

        /**
         * Configure whether to stay silent (stream only Mongo process errors to stdout) or to stream all process output to
         * stdout. By default, only process errors are forwarded to stdout.
         *
         * @param silent
         * @return
         */
        fun withSilent(silent: Boolean): Builder {
            this.silent = silent
            return this
        }

        fun configure(): TestResource {
            if (serverPorts.size > 1 || StringUtils.hasText(replicaSetName)) {
                val rsName = if (StringUtils.hasText(replicaSetName)) replicaSetName else DEFAULT_REPLICA_SET_NAME
                return ReplSet(version, rsName, silent, *serverPorts.toIntArray())
            }
            throw UnsupportedOperationException("implement me")
        }

        init {
            version = VERSION
            serverPorts = emptyList()
            configServerPorts = emptyList()
        }
    }

    /**
     * Interface specifying a test resource which exposes lifecycle methods and connection coordinates.
     */
    interface TestResource {
        /**
         * Start the resource.
         */
        fun start()

        /**
         * Stop the resource.
         */
        fun stop()

        /**
         * @return the connection string to configure a MongoDB client.
         */
        fun connectionString(): String
        fun mongoClient(): MongoClient {
            return MongoClients.create(connectionString())
        }
    }

    internal class ReplSet(private val serverVersion: IFeatureAwareVersion, private val replicaSetName: String, silent: Boolean, vararg serverPorts: Int) : TestResource {
        private val configServerReplicaSetName: String
        private val mongosPort: Int
        private val serverPorts: Array<Int>?
        private val configServerPorts: Array<Int>?
        private var outputFunction: Function<Command, ProcessOutput>
        private var mongosTestFactory: MongosSystemForTestFactory? = null
        fun defaultPortsIfRequired(ports: Array<Int>?): Array<Int>? {
            return if (!ObjectUtils.isEmpty(ports)) {
                ports
            } else try {
                arrayOf(Network.getFreeServerPort(), Network.getFreeServerPort(), Network.getFreeServerPort())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        override fun start() {
            if (mongosTestFactory != null) {
                return
            }
            doStart()
        }

        private fun doStart() {
            val replicaSets: MutableMap<String, List<IMongodConfig>> = LinkedHashMap()
            replicaSets[configServerReplicaSetName] = initConfigServers()
            replicaSets[replicaSetName] = initReplicaSet()

            // create mongos
            val mongosConfig = defaultMongosConfig(serverVersion, mongosPort, defaultCommandOptions(),
                    configServerReplicaSetName, configServerPorts!![0])
            mongosTestFactory = MongosSystemForTestFactory(mongosConfig, replicaSets, emptyList(),
                    DEFAULT_SHARDING, DEFAULT_SHARDING, DEFAULT_SHARD_KEY, outputFunction)
            try {
                LOGGER.info(String.format("Starting config servers at ports %s",
                        StringUtils.arrayToCommaDelimitedString(configServerPorts)))
                LOGGER.info(String.format("Starting replica set '%s' servers at ports %s", replicaSetName,
                        StringUtils.arrayToCommaDelimitedString(serverPorts)))
                mongosTestFactory!!.start()
                LOGGER
                        .info(String.format("Replica set '%s' started. Connection String: %s", replicaSetName, connectionString()))
            } catch (e: Throwable) {
                throw RuntimeException(" Error while starting cluster. ", e)
            }
        }

        private fun initReplicaSet(): List<IMongodConfig> {
            val rs: MutableList<IMongodConfig> = ArrayList()
            for (port in serverPorts!!) {
                rs.add(defaultMongodConfig(serverVersion, port, defaultCommandOptions(), false, true, replicaSetName))
            }
            return rs
        }

        private fun initConfigServers(): List<IMongodConfig> {
            val configServers: MutableList<IMongodConfig> = ArrayList(configServerPorts!!.size)
            for (port in configServerPorts) {
                configServers.add(
                        defaultMongodConfig(serverVersion, port, defaultCommandOptions(), true, false, configServerReplicaSetName))
            }
            return configServers
        }

        override fun stop() {
            if (mongosTestFactory != null) {
                LOGGER.info(String.format("Stopping replica set '%s' servers at ports %s", replicaSetName,
                        StringUtils.arrayToCommaDelimitedString(serverPorts)))
                mongosTestFactory!!.stop()
            }
        }

        override fun connectionString(): String {
            return "mongodb://localhost:" + serverPorts!![0] + "/?replicaSet=" + replicaSetName
        }

        companion object {
            private const val DEFAULT_SHARDING = "none"
            private const val DEFAULT_SHARD_KEY = "_class"
        }

        init {
            this.serverPorts = defaultPortsIfRequired(serverPorts.toTypedArray())
            configServerPorts = defaultPortsIfRequired(null)
            configServerReplicaSetName = DEFAULT_CONFIG_SERVER_REPLICA_SET_NAME
            mongosPort = randomOrDefaultServerPort()
            outputFunction = if (silent) {
                Function { it: Command ->
                    ProcessOutput(Processors.silent(),
                            Processors.namedConsole("[ " + it.commandName() + " error]"), Processors.console())
                }
            } else {
                Function { it: Command -> ProcessOutput.getDefaultInstance(it.commandName()) }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmbeddedMongo::class.java)
        private const val LOCALHOST = "127.0.0.1"
        private const val DEFAULT_REPLICA_SET_NAME = "rs0"
        private const val DEFAULT_CONFIG_SERVER_REPLICA_SET_NAME = "rs-config"
        private const val STORAGE_ENGINE = "wiredTiger"
        private val VERSION = Versions.withFeatures(GenericVersion("4.0.2"),
                Feature.ONLY_WITH_SSL, Feature.ONLY_64BIT, Feature.NO_HTTP_INTERFACE_ARG, Feature.STORAGE_ENGINE,
                Feature.MONGOS_CONFIGDB_SET_STYLE, Feature.NO_CHUNKSIZE_ARG)

        /**
         * Create a new [Builder] to build [EmbeddedMongo].
         *
         * @return
         */
        fun builder(): Builder {
            return Builder()
        }

        /**
         * Create a new [Builder] that is initialized as replica set to build [EmbeddedMongo].
         *
         * @return
         */
        @JvmOverloads
        fun replSet(replicaSetName: String = DEFAULT_REPLICA_SET_NAME): Builder {
            return Builder().withReplicaSetName(replicaSetName)
        }

        private fun randomOrDefaultServerPort(): Int {
            return try {
                Network.getFreeServerPort()
            } catch (e: IOException) {
                27017
            }
        }

        /**
         * @return Default [command options][IMongoCmdOptions].
         */
        private fun defaultCommandOptions(): IMongoCmdOptions {
            return MongoCmdOptionsBuilder() //
                    .useNoPrealloc(false) //
                    .useSmallFiles(false) //
                    .useNoJournal(false) //
                    .useStorageEngine(STORAGE_ENGINE) //
                    .verbose(false) //
                    .build()
        }

        /**
         * Create a default `mongod` config.
         *
         * @param version
         * @param port
         * @param cmdOptions
         * @param configServer
         * @param shardServer
         * @param replicaSet
         * @return
         */
        private fun defaultMongodConfig(version: IFeatureAwareVersion, port: Int, cmdOptions: IMongoCmdOptions,
                                        configServer: Boolean, shardServer: Boolean, replicaSet: String?): IMongodConfig {
            return try {
                var builder = MongodConfigBuilder() //
                        .version(version) //
                        .withLaunchArgument("--quiet") //
                        .net(Net(LOCALHOST, port, Network.localhostIsIPv6())) //
                        .configServer(configServer).cmdOptions(cmdOptions) //
                if (StringUtils.hasText(replicaSet)) {
                    builder = builder //
                            .replication(Storage(null, replicaSet, 0))
                    builder = if (!configServer) {
                        builder.shardServer(shardServer)
                    } else {
                        builder.shardServer(false)
                    }
                }
                builder.build()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        /**
         * Create a default `mongos` config.
         *
         * @param version
         * @param port
         * @param cmdOptions
         * @param configServerReplicaSet
         * @param configServerPort
         * @return
         */
        private fun defaultMongosConfig(version: IFeatureAwareVersion, port: Int, cmdOptions: IMongoCmdOptions,
                                        configServerReplicaSet: String, configServerPort: Int): IMongosConfig {
            return try {
                var builder = MongosConfigBuilder() //
                        .version(version) //
                        .withLaunchArgument("--quiet", null) //
                        .net(Net(LOCALHOST, port, Network.localhostIsIPv6())) //
                        .cmdOptions(cmdOptions)
                if (StringUtils.hasText(configServerReplicaSet)) {
                    builder = builder.replicaSet(configServerReplicaSet) //
                            .configDB("$LOCALHOST:$configServerPort")
                }
                builder.build()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
