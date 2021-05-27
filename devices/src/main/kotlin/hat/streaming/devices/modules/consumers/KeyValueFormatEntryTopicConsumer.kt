package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.consumers.transformers.KeyValueMessageParser
import hat.streaming.devices.modules.service.IOTSignalProcessorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service


@Service
class KeyValueFormatEntryTopicConsumer (val parser: KeyValueMessageParser,
                                        val processorService: IOTSignalProcessorService) {

    val logger: Logger = LoggerFactory.getLogger(KeyValueFormatEntryTopicConsumer::class.java)

    @KafkaListener(topics = ["device_events_entry_key_value"], containerFactory = "keyValueEntryTopicContainerFactory")
    fun consumeEntryJsonTopic(@Payload deviceSignals: List<String>): Unit = runBlocking(Dispatchers.Default) {

        deviceSignals.forEach { signalStr ->
            // convert string message to BaseIOTSignal
            val signal = parser.parseKeyValueMessage(signalStr)
            // process message
            launch(Dispatchers.IO) { processorService.processIOTSignal(signal) }
        }
    }
}
