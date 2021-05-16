package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.dbrepos.ClusterDeviceRepo
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
class DeviceClusterTopicConsumer(val clusterDeviceRepo: ClusterDeviceRepo) {

    val logger: Logger = LoggerFactory.getLogger(DeviceClusterTopicConsumer::class.java)

    @KafkaListener(topics = ["device_cluster_faulty_topic"], containerFactory = "deviceClusterContainerFactory")
    fun consumeClusterSignalTopic(@Payload deviceSignals: List<IOTDeviceSignal>
                      , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                      , @Header(KafkaHeaders.OFFSET) offsets: List<Long> ): Unit = runBlocking(Dispatchers.Default) {

        with(logger) {
            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
            info("beginning to consume batch size: {} ", deviceSignals.size)
            for (i in deviceSignals.indices) {
                info(
                    "received message='{}' partition={}, offset= {} ",  deviceSignals[i]
                    ,partitions[i].toString(), offsets[i]
                )
            }

            clusterDeviceRepo.saveClusterDeviceSignals(deviceSignals)
            info("all batch messages consumed")
        }

//        val result = deviceSignals.map {  }
    }
}
