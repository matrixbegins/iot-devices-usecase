package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.service.AllSignalDBService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class AllSignalTypeConsumer(val allSignalDBService: AllSignalDBService) {

    val logger: Logger = LoggerFactory.getLogger(AllSignalTypeConsumer::class.java)

    @KafkaListener(topicPattern = ".*_metric_tracker",
        containerFactory = "allSignalTypeContainerFactory" )
    fun consumeEntryJsonTopic(@Payload signals: List<IOTDeviceSignal>
                              , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                              , @Header(KafkaHeaders.OFFSET) offsets: List<Long>
                              , @Header(KafkaHeaders.RECEIVED_TOPIC) topics: List<String> ): Unit = runBlocking(
        Dispatchers.Default) {

        with(logger) {
            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
            info("beginning process metric tracker messages : {} ", signals.size)

            for (i in signals.indices) {
                info(
                    "received message='{}' partition={}, offset= {} at kafka topic = {} ",
                    signals[i],
                    partitions[i].toString(),
                    offsets[i],
                    topics[i]
                )
            }
            info("metric tracker messages processed")
        }

        val signalMap = signals.groupBy { it.signalType }
        for ((signalType, signalSubset) in signalMap) {
            launch { allSignalDBService.saveMetricTrackerSignals(signalType.toString(), signalSubset) }
        }
    }

}