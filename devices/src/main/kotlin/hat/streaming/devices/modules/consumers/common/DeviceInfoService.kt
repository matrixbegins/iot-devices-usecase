package hat.streaming.devices.modules.consumers.common

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitResponseResult
import hat.streaming.devices.modules.dto.DeviceInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeviceInfoService {

    val logger: Logger = LoggerFactory.getLogger(DeviceInfoService::class.java)

    suspend fun getDeviceInfo(deviceId: String): DeviceInfo =
                    Fuel.get("http://localhost:7795/api/rest/device/$deviceId")
                        .awaitResponseResult(DeviceInfo.DeviceInfoDeserializer)
                        .third.get()

}