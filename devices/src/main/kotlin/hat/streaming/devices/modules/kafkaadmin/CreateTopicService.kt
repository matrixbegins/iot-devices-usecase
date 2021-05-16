package hat.streaming.devices.modules.kafkaadmin

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
@Component
class CreateTopicService() {

    @Value("\${spring.kafka.bootstrap-servers:}")
    val bootstrapServers: String = ""

//    @Bean
//    fun admin() = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers ))
//
//    suspend fun createTopic(): Boolean {
//
//        val client = KafkaAdmin().
//
//        return true;
//    }

}