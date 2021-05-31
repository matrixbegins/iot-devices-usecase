package hat.streaming.devices.modules.service

import hat.streaming.devices.modules.consumers.common.DeviceInfoService
import hat.streaming.devices.modules.consumers.transformers.MetricConvertor
import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.producers.CompromisedSignalProducer
import hat.streaming.devices.modules.producers.DeviceSignalMainTopicProducer
import hat.streaming.devices.modules.producers.FaultySignalProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class IOTSignalProcessorService(val deviceInfoService: DeviceInfoService,
                                val faultyProducer: FaultySignalProducer,
                                val compromisedProducer: CompromisedSignalProducer,
                                val mainProducer: DeviceSignalMainTopicProducer,
                                val metricConvertor: MetricConvertor) {

    val logger: Logger = LoggerFactory.getLogger(IOTSignalProcessorService::class.java)

    suspend fun processIOTSignal(signal: BaseIOTSignal) {

        val deviceInfo = run {
            logger.debug("getting device info from Device service= {} ", signal.deviceId)
            return@run deviceInfoService.getDeviceInfo(signal.deviceId.toString())
        }
        signal.signalType = deviceInfo.signalType
        // check if signal is compromised.
        if(!signal.validateMessageDigest()) {
            // push data to compromised signal topic
            coroutineScope {
                logger.debug("Device signal is compromised= {}", signal)
                launch(Dispatchers.IO) { compromisedProducer.publishCompromisedSignal(signal) }
            }
            return
        }

        // check if signal is faulty
        if(signal.signalValue < deviceInfo.signalMinValue || signal.signalValue > deviceInfo.signalMaxValue) {
            // push data to faulty signal topic
            coroutineScope {
                logger.debug("Device signal is faulty= {}", signal)
                launch(Dispatchers.IO) { faultyProducer.publishFaultySignal(signal) }
            }
        }

        // we have a valid signal.
        // now quickly do measurement unit/matrices conversion of signalValue
        val iotSignal = metricConvertor.convertSignalMetric(IOTDeviceSignal(deviceInfo, signal))
        // Push it to relevant signalType topic
        coroutineScope {
            logger.debug("publishing to device signal type topic= {}")
            launch(Dispatchers.IO) { mainProducer.publishIOTSignal(iotSignal) }
        }

    }

}