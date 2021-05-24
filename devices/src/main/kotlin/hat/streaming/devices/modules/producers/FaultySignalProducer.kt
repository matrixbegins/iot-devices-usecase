package hat.streaming.devices.modules.producers

import hat.streaming.devices.modules.dto.BaseIOTSignal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class FaultySignalProducer {

    private val FAULTY_TOPIC_NAME = "faulty_signals_topic"

    @Autowired
    lateinit var baseSignalTemplate: KafkaTemplate<String, BaseIOTSignal>

    private val logger: Logger = LoggerFactory.getLogger(FaultySignalProducer::class.java)


    suspend fun publishFaultySignal(signal: BaseIOTSignal){

        val future: ListenableFuture<SendResult<String, BaseIOTSignal>> = baseSignalTemplate.send(FAULTY_TOPIC_NAME, signal)

        with(future) {

            val obj = object : ListenableFutureCallback<SendResult<String, BaseIOTSignal>> {
                override fun onSuccess(message: SendResult<String, BaseIOTSignal>?) {
                    logger.debug("Message published with offset: {}", message?.recordMetadata?.offset())
                }

                override fun onFailure(error: Throwable): Unit {
                    logger.error("Error in publishing message: {}", signal, error)
                }
            }

            addCallback( obj )
        }
    }
}