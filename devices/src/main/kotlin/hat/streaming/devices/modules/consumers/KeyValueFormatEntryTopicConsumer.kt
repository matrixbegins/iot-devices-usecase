package hat.streaming.devices.modules.consumers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service


@Service
class KeyValueFormatEntryTopicConsumer {

    val logger: Logger = LoggerFactory.getLogger(KeyValueFormatEntryTopicConsumer::class.java)

    @KafkaListener(topics = ["device_events_entry_key_value"], containerFactory = "keyValueEntryTopicContainerFactory")
    fun consumeEntryJsonTopic(@Payload deviceSignals: List<String>
                              , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                              , @Header(KafkaHeaders.OFFSET) offsets: List<Long> ): Unit = runBlocking(Dispatchers.Default) {

        logger.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        logger.info("beginning to consume batch size: {} ", deviceSignals.size);

        for (i in deviceSignals.indices) {
            logger.info(
                "received message='{}' partition={}, offset= {} ",  deviceSignals[i]
                ,partitions[i].toString(), offsets[i]
            )
        }
        logger.info("all batch messages consumed")
    }
}
