package hat.streaming.devices.modules.producers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture

@Service
class DeviceSignalMainTopicProducer {
    private val TOPIC_NAME_FORMAT = "##SIGNAL_TYPE##_metric_tracker"

    @Autowired
    lateinit var producerTemplate: KafkaTemplate<String, IOTDeviceSignal>

    private val logger: Logger = LoggerFactory.getLogger(DeviceSignalMainTopicProducer::class.java)

    suspend fun publishIOTSignal(signal: IOTDeviceSignal){
        val topicName = TOPIC_NAME_FORMAT.replace("##SIGNAL_TYPE##", signal.signalType.toString())

        val future: ListenableFuture<SendResult<String, IOTDeviceSignal>> = producerTemplate.send(topicName, signal)
        future.get()
//        coroutineScope { launch { future.get() } }

//        with(future) {
//
//            val obj = object : ListenableFutureCallback<SendResult<String, IOTDeviceSignal>> {
//                override fun onSuccess(message: SendResult<String, IOTDeviceSignal>?) {
//                    logger.debug("Message published with offset: {}", message?.recordMetadata?.offset())
//                }
//
//                override fun onFailure(error: Throwable) {
//                    logger.error("Error in publishing message: {}", signal, error)
//                }
//            }
//            addCallback( obj )
//        }
    }

    suspend fun flushProducer() {
        producerTemplate.flush()
    }
}