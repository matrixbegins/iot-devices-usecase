package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.producers.DeviceSignalMainTopicProducer
import hat.streaming.devices.modules.service.IOTSignalProcessorService
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
class JsonFormatEntryTopicConsumer(val processorService: IOTSignalProcessorService,
                                   val mainProducerService: DeviceSignalMainTopicProducer) {

    val logger: Logger = LoggerFactory.getLogger(JsonFormatEntryTopicConsumer::class.java)

    @KafkaListener(topics = ["device_events_entry_json"], containerFactory = "jsonEntryTopicContainerFactory")
    fun consumeEntryJsonTopic(@Payload deviceSignals: List<BaseIOTSignal>
                      , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                      , @Header(KafkaHeaders.OFFSET) offsets: List<Long> ): Unit = runBlocking(Dispatchers.Default) {

//        with(logger) {
//            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
//            info("beginning to consume batch size: {} ", deviceSignals.size)
//            for (i in deviceSignals.indices) {
//                info(
//                    "received message='{}' partition={}, offset= {} ",  deviceSignals[i]
//                    ,partitions[i].toString(), offsets[i]
//                )
//            }
//            info("all batch messages consumed")
//        }

        deviceSignals.forEach { signal -> launch(Dispatchers.IO) { processorService.processIOTSignal(signal) } }
    }
}