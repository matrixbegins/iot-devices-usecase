package hat.streaming.devices.modules.consumers.common

import hat.streaming.devices.modules.dto.DeviceInfo
import hat.streaming.devices.modules.rest.repos.DeviceInfoRepo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DeviceInfoService(val deviceInfoRepo: DeviceInfoRepo) {

    val logger: Logger = LoggerFactory.getLogger(DeviceInfoService::class.java)

//    @Cacheable(cacheNames = ["device_info_client"], key = "#deviceId")
//    suspend fun getDeviceInfo(deviceId: String): DeviceInfo =
//                    Fuel.get("http://localhost:7795/api/rest/device/$deviceId")
//                        .awaitResponseResult(DeviceInfo.DeviceInfoDeserializer)
//                        .third.get()

    @Cacheable(cacheNames = ["device_info_client"], key = "#deviceId")
    suspend fun getDeviceInfo(deviceId: String): DeviceInfo = deviceInfoRepo.findByDeviceId(deviceId).orElseThrow()

}