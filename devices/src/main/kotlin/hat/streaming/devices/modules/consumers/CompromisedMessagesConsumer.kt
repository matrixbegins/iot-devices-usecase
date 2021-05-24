package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.service.CompromisedSignalService
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
class CompromisedMessagesConsumer(val compromisedSignalService: CompromisedSignalService) {

    val logger: Logger = LoggerFactory.getLogger(CompromisedMessagesConsumer::class.java)

    @KafkaListener(topics = ["tampered_signals_topic"], containerFactory = "jsonEntryTopicContainerFactory")
    fun consumeEntryJsonTopic(@Payload faultySignals: List<BaseIOTSignal>
                              , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                              , @Header(KafkaHeaders.OFFSET) offsets: List<Long> ): Unit = runBlocking(Dispatchers.Default) {

//        with(logger) {
//            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
//            info("beginning to process compromised messages : {} ", faultySignals.size)
//
//            for (i in faultySignals.indices) {
//                info(
//                    "received message='{}' partition={}, offset= {} ",
//                    faultySignals[i],
//                    partitions[i].toString(),
//                    offsets[i]
//                )
//            }
//        }

        launch(Dispatchers.IO) { compromisedSignalService.saveSignals(faultySignals) }
        logger.debug("compromised messages processed")
    }

}