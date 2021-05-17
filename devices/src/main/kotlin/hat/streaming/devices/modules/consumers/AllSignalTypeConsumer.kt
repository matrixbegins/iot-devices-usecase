package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.BaseIOTSignal
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
class AllSignalTypeConsumer {

    val logger: Logger = LoggerFactory.getLogger(AllSignalTypeConsumer::class.java)

    @KafkaListener(topicPattern = ".*_metric_tracker", containerFactory = "deviceClusterContainerFactory")
    fun consumeEntryJsonTopic(@Payload faultySignals: List<BaseIOTSignal>
                              , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                              , @Header(KafkaHeaders.OFFSET) offsets: List<Long>
                              , @Header(KafkaHeaders.RECEIVED_TOPIC) topics: List<String> ): Unit = runBlocking(
        Dispatchers.Default) {

        with(logger) {
            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
            info("beginning process metric tracker messages : {} ", faultySignals.size);

            for (i in faultySignals.indices) {
                info(
                    "received message='{}' partition={}, offset= {} at kafka topic = {} ",
                    faultySignals[i],
                    partitions[i].toString(),
                    offsets[i],
                    topics[i]
                )
            }
            info("metric tracker messages processed")
        }
    }

}