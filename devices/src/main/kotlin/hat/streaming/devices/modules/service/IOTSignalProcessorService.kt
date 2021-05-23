package hat.streaming.devices.modules.service

import hat.streaming.devices.modules.consumers.common.DeviceInfoService
import hat.streaming.devices.modules.consumers.transformers.MetricConvertor
import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.producers.CompromisedSignalProducer
import hat.streaming.devices.modules.producers.DeviceSignalMainTopicProducer
import hat.streaming.devices.modules.producers.FaultySignalProducer
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

    suspend fun processIOTSignal(signal: BaseIOTSignal): Unit {

        val deviceInfo = run {
            logger.debug("getting device info from Device service= {} ", signal.deviceId)
            return@run deviceInfoService.getDeviceInfo(signal.deviceId.toString())
        }
        signal.signalType = deviceInfo.signalType
        // check if signal is compromised.
        if(!signal.validateMessageDigest()) {
            // push data to compromised signal topic
            logger.debug("Device signal is compromised= {}", signal)
            compromisedProducer.publishCompromisedSignal(signal)
            return
        }

        // check if signal is faulty
        if(signal.signalValue < deviceInfo.signalMinValue || signal.signalValue > deviceInfo.signalMaxValue) {
            // push data to faulty signal topic
            logger.debug("Device signal is faulty= {}", signal)
            faultyProducer.publishFaultySignal(signal)

        }

        // we have a valid signal.
        // now quickly do measurement unit/matrices conversion of signalValue
        val iotSignal = metricConvertor.convertSignalMetric(IOTDeviceSignal(deviceInfo, signal))
//      logger.info("publishing to device signal type topic= {}")
        // Push it to relevant signalType topic
        mainProducer.publishIOTSignal(iotSignal)

    }

}