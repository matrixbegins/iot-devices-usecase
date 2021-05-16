package hat.streaming.devices.modules.dto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.ResponseDeserializable
import kotlinx.serialization.Serializable

@Serializable
class DeviceInfo(
    var deviceId:  String? = null,
    var signalType: String? = null,
    var signalUnit: String? = null,
    var deviceCluster: String? = null,
    var orgId: String? = null,
    var facilityId: String? = null,
    var signalMinValue: Double,
    var signalMaxValue: Double
) {
    object DeviceInfoDeserializer : ResponseDeserializable<DeviceInfo> {
        override fun deserialize(content: String) =
            jacksonObjectMapper().readValue<DeviceInfo>(content)
    }
}

