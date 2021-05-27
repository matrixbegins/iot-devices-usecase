package hat.streaming.devices.modules.kafkaadmin

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
@Component
class CreateTopicService {

    @Value("\${spring.kafka.bootstrap-servers:}")
    val bootstrapServers: String = ""

    @Value("\${spring.kafka.new-topic-config.partition-count:}")
    val newTopicPartition: Int = 1

    @Value("\${spring.kafka.new-topic-config.replica-count:}")
    val newTopicReplica: Short = 1

    val logger: Logger = LoggerFactory.getLogger(CreateTopicService::class.java)

    fun admin(): AdminClient = AdminClient.create(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers ))

    suspend fun createTopic(topicName: String): Boolean {
        val response = admin().createTopics( listOf(getNewTopic(topicName)) ).all()
        response.get()
        return true
    }

    fun getNewTopic(topicName: String): NewTopic {
        logger.info("Creating new partition with name={} partitionCount={}, replicas= {} ", topicName, newTopicPartition, newTopicReplica)
        return NewTopic(topicName, newTopicPartition, newTopicReplica)
    }

}