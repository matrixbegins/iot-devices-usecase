package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.service.ClusterDeviceDBService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service


@Service
class DeviceClusterTopicConsumer(val clusterDeviceDBService: ClusterDeviceDBService) {

    val logger: Logger = LoggerFactory.getLogger(DeviceClusterTopicConsumer::class.java)

    @KafkaListener(topics = ["device_cluster_faulty_topic"], containerFactory = "deviceClusterContainerFactory")
    fun consumeClusterSignalTopic(@Payload deviceSignals: List<IOTDeviceSignal> ): Unit = runBlocking(Dispatchers.Default) {

        launch(Dispatchers.IO) { clusterDeviceDBService.saveClusterDeviceSignals(deviceSignals) }
        logger.debug("all batch messages consumed")

    }
}
