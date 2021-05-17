package hat.streaming.devices.modules.consumers

import hat.streaming.devices.modules.consumers.common.DeviceInfoService
import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.producers.CompromisedSignalProducer
import hat.streaming.devices.modules.producers.DeviceSignalMainTopicProducer
import hat.streaming.devices.modules.producers.FaultySignalProducer
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
class JsonFormatEntryTopicConsumer(val deviceInfoService: DeviceInfoService,
                                    val faultyProducer: FaultySignalProducer,
                                    val compromisedProducer: CompromisedSignalProducer,
                                    val mainProducer: DeviceSignalMainTopicProducer) {

    val logger: Logger = LoggerFactory.getLogger(JsonFormatEntryTopicConsumer::class.java)

    @KafkaListener(topics = ["device_events_entry_json"], containerFactory = "jsonEntryTopicContainerFactory")
    fun consumeEntryJsonTopic(@Payload deviceSignals: List<BaseIOTSignal>
                      , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitions: List<Int>
                      , @Header(KafkaHeaders.OFFSET) offsets: List<Long> ): Unit = runBlocking(Dispatchers.Default) {

//        with(logger) {
//            info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
//            info("beginning to consume batch size: {} ", deviceSignals.size)
//            for (i in deviceSignals.indices) {
//                info(
//                    "received message='{}' partition={}, offset= {} ",  deviceSignals[i]
//                    ,partitions[i].toString(), offsets[i]
//                )
//            }
//            info("all batch messages consumed")
//        }
        deviceSignals.forEach { signal ->
            // get device Info of the signal
            val deviceInfo = run {
                logger.info("getting device info from Device service= {} ", signal.deviceId)
                return@run deviceInfoService.getDeviceInfo(signal.deviceId.toString())
            }
            signal.signalType = deviceInfo.signalType
            // check if signal is faulty
            if(signal.signalValue < deviceInfo.signalMinValue || signal.signalValue > deviceInfo.signalMaxValue) {
                // push data to faulty signal topic
                logger.info("Device signal is faulty= {}", signal)
                faultyProducer.publishFaultySignal(signal)
                return@forEach
            }
            // check if signal is compromised.
            if(!signal.validateMessageDigest()) {
                // push data to compromised signal topic
                logger.info("Device signal is compromised= {}", signal)
                compromisedProducer.publishCompromisedSignal(signal)
                return@forEach
            }

            // we have a valid signal. Push it to relevant signalType topic
            val iotSignal = IOTDeviceSignal(deviceInfo, signal)
//            logger.info("publishing to device signal type topic= {}")
            mainProducer.publishIOTSignal(iotSignal)
        }

    }

}
