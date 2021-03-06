package hat.streaming.devices.modules.rest.controllers


import hat.streaming.devices.modules.consumers.common.DeviceInfoService
import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.DeviceInfo
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import hat.streaming.devices.modules.kafkaadmin.CreateTopicService
import hat.streaming.devices.modules.rest.dto.MsgResponse
import hat.streaming.devices.modules.rest.models.DeviceTypes
import hat.streaming.devices.modules.rest.repos.DeviceInfoRepo
import hat.streaming.devices.modules.rest.repos.DeviceTypeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class HomeController(val deviceTypeRepo: DeviceTypeRepo,
                     val deviceInfoRepo: DeviceInfoRepo,
                     val deviceInfoService: DeviceInfoService,
                     val createTopicService: CreateTopicService) {

    @Autowired
    lateinit var baseSignalTemplate: KafkaTemplate<String, BaseIOTSignal>

    @Autowired
    lateinit var mainSignalTemplate: KafkaTemplate<String, IOTDeviceSignal>

    private val logger: Logger = LoggerFactory.getLogger(HomeController::class.java)

    @GetMapping("/home")
    @Cacheable(cacheNames = ["device_types"])
    fun homeRoute(): MutableIterable<DeviceTypes> {
        logger.info("route home................")
        return deviceTypeRepo.findAll()
    }

    @GetMapping("/proxy")
    fun proxyRoute(): Any = runBlocking(Dispatchers.Default) {
        logger.info("route proxy ................")
        return@runBlocking deviceInfoService.getDeviceInfo("E6HUM23Z4X")
    }

    @GetMapping("/create-topic")
    fun createTopicRoute(): Any = runBlocking(Dispatchers.Default) {
        logger.info("route proxy ................")
        return@runBlocking createTopicService.createTopic("some_unknown_topic")
    }

    @GetMapping("/device/{deviceId}")
    @Cacheable(cacheNames = ["device_info"], key = "#deviceId")
    fun deviceInfo(@PathVariable("deviceId") deviceId: String): DeviceInfo = runBlocking(Dispatchers.Default) {
        logger.info("getting device Info for {} :: ", deviceId)
        return@runBlocking deviceInfoRepo.findByDeviceId(deviceId).orElseThrow()
    }

    @GetMapping("/devices/org/{orgId}/facility/{facilityId}")
    @Cacheable(cacheNames = ["device_info_org_facility"], key = "new org.springframework.cache.interceptor.SimpleKey(#orgId, #facilityId)")
    fun deviceInfoByOrgAndFacility(@PathVariable("orgId") orgId: String,
                                   @PathVariable("facilityId") facilityId: String ): Iterable<DeviceInfo> {
        logger.info("getting all devices for org: {} and facility {} :: ", orgId, facilityId)
        return deviceInfoRepo.findByOrgIdAndFacilityId(orgId, facilityId)
    }

    @GetMapping("/send/signal/faulty")
    fun sendFaultyBaseIotSignalToKafka(): MsgResponse {

        val payload = BaseIOTSignal(400.00, "KKTTPPII", 1620987265691, null, "temperature")

        val future: ListenableFuture<SendResult<String, BaseIOTSignal>> = baseSignalTemplate.send("faulty_signals_topic", payload)

        with(future) {

            val obj = object : ListenableFutureCallback<SendResult<String, BaseIOTSignal>> {
                override fun onSuccess(message: SendResult<String, BaseIOTSignal>?) {
                    logger.info("Message published with offset: {}", message?.recordMetadata?.offset())
                }

                override fun onFailure(error: Throwable) {
                    logger.error("Error in publishing message: {}", payload, error)
                }
            }
            addCallback( obj )
        }

        return MsgResponse(200, "Msg Sent")
    }


    @GetMapping("/send/signal/tampered")
    fun sendCompromisedBaseIotSignalToKafka(): MsgResponse {

        val payload = BaseIOTSignal(178.00, "TPDSG56GFBD", 1620987265691, "sdbfvkjdf565kjvndjkvn2dfbirbavadve65", "pressure")

        val future: ListenableFuture<SendResult<String, BaseIOTSignal>> = baseSignalTemplate.send("auto_topic_test", payload)

        with(future) {

            val obj = object : ListenableFutureCallback<SendResult<String, BaseIOTSignal>> {
                override fun onSuccess(message: SendResult<String, BaseIOTSignal>?) {
                    logger.info("Message published with offset: {}", message?.recordMetadata?.offset())
                }

                override fun onFailure(error: Throwable) {
                    logger.error("Error in publishing message: {}", payload, error)
                }
            }
            addCallback( obj )
        }

        return MsgResponse(200, "Msg Sent")
    }


    @GetMapping("/send/signal/temperature/json")
    fun sendJsonTemperatureDeviceSignal(): MsgResponse = runBlocking(Dispatchers.Default)  {

        val payload = BaseIOTSignal(300.00, "JWWMFAJQ8B", System.currentTimeMillis(), "sdbfvkjdf565kjvndjkvn2dfbirbavadve65")

        baseSignalTemplate.send("device_events_entry_json", payload)

        return@runBlocking MsgResponse(200, "Msg Sent")
    }


    @GetMapping("/send/signal/main")
    fun sendDeviceMetricSignal(): MsgResponse = runBlocking(Dispatchers.Default)  {

        val payload = IOTDeviceSignal("C", "Boiler-4ZQH2", "NUCLEAR-INC", "Boston",
                        230.00, "4QFXB9MEZA", System.currentTimeMillis(), "temperature")

        mainSignalTemplate.send("temperature_metric_tracker", payload)

        payload.signalType = "pressure"
        payload.signalUnit = "ATM"
        payload.signalValue = 3.56
        mainSignalTemplate.send("pressure_metric_tracker", payload)

        return@runBlocking MsgResponse(200, "Msg Sent")
    }

}

