package hat.streaming.devices.modules.producers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class DeviceSignalMainTopicProducer {
    private val TOPIC_NAME_FORMAT = "##SIGNAL_TYPE##_metric_tracker"

    @Autowired
    lateinit var producerTemplate: KafkaTemplate<String, IOTDeviceSignal>

    private val logger: Logger = LoggerFactory.getLogger(DeviceSignalMainTopicProducer::class.java)

    suspend fun publishIOTSignal(signal: IOTDeviceSignal){
        val topicName = TOPIC_NAME_FORMAT.replace("##SIGNAL_TYPE##", signal.signalType.toString())



    }

}