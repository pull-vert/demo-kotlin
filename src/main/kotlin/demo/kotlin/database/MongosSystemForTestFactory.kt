package demo.kotlin.database

import com.mongodb.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.SocketSettings
import de.flapdoodle.embed.mongo.*
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.IMongosConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder
import de.flapdoodle.embed.process.config.io.ProcessOutput
import org.bson.Document
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function


internal class MongosSystemForTestFactory(private val config: IMongosConfig,
                                          private val replicaSets: Map<String, List<IMongodConfig>>,
                                          private val configServers: List<IMongodConfig>, private val shardDatabase: String,
                                          private val shardCollection: String, private val shardKey: String, private val outputFunction: Function<Command, ProcessOutput>) {
    private lateinit var mongosExecutable: MongosExecutable
    private var mongosProcess: MongosProcess? = null
    private var mongodProcessList: MutableList<MongodProcess>? = null
    private var mongodConfigProcessList: List<MongodProcess>? = null

    @Throws(Throwable::class)
    fun start() {
        mongodProcessList = ArrayList()
        mongodConfigProcessList = ArrayList()
        for (entry in replicaSets.entries) {
            initializeReplicaSet(entry)
        }
        for (config in configServers) {
            initializeConfigServer(config)
        }
        initializeMongos()
        configureMongos()
    }

    @Throws(Exception::class)
    private fun initializeReplicaSet(entry: Map.Entry<String, List<IMongodConfig>>) {
        val replicaName = entry.key
        val mongoConfigList = entry.value
        if (mongoConfigList.size < 3) {
            throw Exception(
                    "A replica set must contain at least 3 members.")
        }
        // Create 3 mongod processes
        for (mongoConfig in mongoConfigList) {
            if (mongoConfig.replication().replSetName != replicaName) {
                throw Exception(
                        "Replica set name must match in mongo configuration")
            }
            val runtimeConfig = RuntimeConfigBuilder()
                    .defaultsWithLogger(Command.MongoD, logger)
                    .processOutput(outputFunction.apply(Command.MongoD))
                    .build()
            val starter = MongodStarter.getInstance(runtimeConfig)
            val mongodExe = starter.prepare(mongoConfig)
            val process = mongodExe.start()
            mongodProcessList!!.add(process)
        }
        Thread.sleep(1000)
        val mo = MongoClientSettings.builder()
                .applyToSocketSettings { builder: SocketSettings.Builder -> builder.connectTimeout(10, TimeUnit.SECONDS) }.applyToClusterSettings { builder: ClusterSettings.Builder -> builder.hosts(listOf(toAddress(mongoConfigList[0].net()))) }
                .build()
        val mongo: MongoClient = MongoClients.create(mo)
        val mongoAdminDB = mongo.getDatabase(ADMIN_DATABASE_NAME)
        var cr = mongoAdminDB.runCommand(Document("isMaster", 1))
        logger.info("isMaster: {}", cr)

        // Build BSON object replica set settings
        val replicaSetSetting: DBObject = BasicDBObject()
        replicaSetSetting.put("_id", replicaName)
        val members = BasicDBList()
        var i = 0
        for (mongoConfig in mongoConfigList) {
            val host: DBObject = BasicDBObject()
            host.put("_id", i++)
            host.put("host", mongoConfig.net().serverAddress.hostName
                    + ":" + mongoConfig.net().port)
            members.add(host)
        }
        replicaSetSetting.put("members", members)
        logger.info(replicaSetSetting.toString())
        // Initialize replica set
        cr = mongoAdminDB.runCommand(Document("replSetInitiate",
                replicaSetSetting))
        logger.info("replSetInitiate: {}", cr)
        Thread.sleep(5000)
        cr = mongoAdminDB.runCommand(Document("replSetGetStatus", 1))
        logger.info("replSetGetStatus: {}", cr)

        // Check replica set status before to proceed
        while (!isReplicaSetStarted(cr)) {
            logger.info("Waiting for 3 seconds...")
            Thread.sleep(1000)
            cr = mongoAdminDB.runCommand(Document("replSetGetStatus", 1))
            logger.info("replSetGetStatus: {}", cr)
        }
        mongo.close()
    }

    private fun isReplicaSetStarted(setting: Document): Boolean {
        if (setting["members"] == null) {
            return false
        }
        val members = setting["members"] as List<*>?
        for (m in members!!) {
            val member = m as Document
            logger.info(member.toString())
            val state = member.getInteger("state", 0)
            logger.info("state: {}", state)
            // 1 - PRIMARY, 2 - SECONDARY, 7 - ARBITER
            if (state != 1 && state != 2 && state != 7) {
                return false
            }
        }
        return true
    }

    @Throws(Exception::class)
    private fun initializeConfigServer(config: IMongodConfig) {
        if (!config.isConfigServer) {
            throw Exception(
                    "Mongo configuration is not a defined for a config server.")
        }
        val starter = MongodStarter.getDefaultInstance()
        val mongodExe = starter.prepare(config)
        val process = mongodExe.start()
        mongodProcessList!!.add(process)
    }

    @Throws(Exception::class)
    private fun initializeMongos() {
        val runtime = MongosStarter.getInstance(RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoS, logger)
                .processOutput(outputFunction.apply(Command.MongoS))
                .build())
        mongosExecutable = runtime.prepare(config)
        mongosProcess = mongosExecutable.start()
    }

    @Throws(Exception::class)
    private fun configureMongos() {
        var cr: Document
        val options = MongoClientSettings.builder()
                .applyToSocketSettings { builder: SocketSettings.Builder -> builder.connectTimeout(10, TimeUnit.SECONDS) }
                .applyToClusterSettings { builder: ClusterSettings.Builder -> builder.hosts(listOf(toAddress(config.net()))) }
                .build()
        MongoClients.create(options).use { mongo ->
            val mongoAdminDB = mongo.getDatabase(ADMIN_DATABASE_NAME)

            // Add shard from the replica set list
            for ((replicaName, value) in replicaSets) {
                var command = ""
                for (mongodConfig in value) {
                    if (command.isEmpty()) {
                        command = "$replicaName/"
                    } else {
                        command += ","
                    }
                    command += (mongodConfig.net().serverAddress.hostName
                            + ":" + mongodConfig.net().port)
                }
                logger.info("Execute add shard command: {}", command)
                cr = mongoAdminDB.runCommand(Document("addShard", command))
                logger.info(cr.toString())
            }
            logger.info("Execute list shards.")
            cr = mongoAdminDB.runCommand(Document("listShards", 1))
            logger.info(cr.toString())

            // Enabled sharding at database level
            logger.info("Enabled sharding at database level")
            cr = mongoAdminDB.runCommand(Document("enableSharding",
                    shardDatabase))
            logger.info(cr.toString())

            // Create index in sharded collection
            logger.info("Create index in sharded collection")
            val db = mongo.getDatabase(shardDatabase)
            db.getCollection(shardCollection).createIndex(Document(shardKey, 1))

            // Shard the collection
            logger.info("Shard the collection: {}.{}", shardDatabase, shardCollection)
            val cmd = Document()
            cmd["shardCollection"] = shardDatabase + "." + shardCollection
            cmd["key"] = BasicDBObject(shardKey, 1)
            cr = mongoAdminDB.runCommand(cmd)
            logger.info(cr.toString())
            logger.info("Get info from config/shards")
            val cursor = mongo.getDatabase("config").getCollection("shards").find()
            val iterator = cursor.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                logger.info(item.toString())
            }
        }
    }

    fun stop() {
        for (process in mongodProcessList!!) {
            process.stop()
        }
        for (process in mongodConfigProcessList!!) {
            process.stop()
        }
        mongosProcess!!.stop()
    }

    companion object {
        private val logger = LoggerFactory
                .getLogger(MongosSystemForTestFactory::class.java)
        const val ADMIN_DATABASE_NAME = "admin"
        const val LOCAL_DATABASE_NAME = "local"
        const val REPLICA_SET_NAME = "rep1"
        const val OPLOG_COLLECTION = "oplog.rs"

        private fun toAddress(net: Net): ServerAddress {
            return ServerAddress(net.serverAddress, net.port)
        }
    }

}
