package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.service.AllSignalDBService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class AllSignalTypeConsumer(val allSignalDBService: AllSignalDBService) {

    val logger: Logger = LoggerFactory.getLogger(AllSignalTypeConsumer::class.java)

    @KafkaListener(topicPattern = ".*_metric_tracker", containerFactory = "allSignalTypeContainerFactory" )
    fun consumeEntryJsonTopic(@Payload signals: List<IOTDeviceSignal> ): Unit = runBlocking(Dispatchers.Default) {

        val signalMap = signals.groupBy { it.signalType }
        for ((signalType, signalSubset) in signalMap) {
            launch(Dispatchers.IO) { allSignalDBService.saveMetricTrackerSignals(signalType.toString(), signalSubset) }
        }
    }

}