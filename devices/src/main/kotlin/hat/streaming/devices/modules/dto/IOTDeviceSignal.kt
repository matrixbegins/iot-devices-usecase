package hat.streaming.devices.modules.dto

import kotlinx.serialization.Serializable

@Serializable
class IOTDeviceSignal(
    var signalUnit: String? = null,
    var deviceCluster: String? = null,
    var orgId: String? = null,
    var facilityId: String? = null,
    signalValue: Double,
    deviceId:  String? = null,
    timestamp: Long,
    signalType: String? = null
): BaseIOTSignal(signalValue, deviceId, timestamp, "NA", "NA") {

    constructor(deviceInfo: DeviceInfo, baseSignal: BaseIOTSignal) : this(
            deviceInfo.signalUnit,
            deviceInfo.deviceCluster,
            deviceInfo.orgId,
            deviceInfo.facilityId,

            baseSignal.signalValue,
            baseSignal.deviceId,
            baseSignal.timestamp,
            deviceInfo.signalType
        )

    override fun toString(): String {
        return "IOTDeviceSignal(deviceId=$deviceId, signalValue=$signalValue, signalUnit=$signalUnit, deviceCluster=$deviceCluster, orgId=$orgId, facilityId=$facilityId, timestamp=$timestamp, messageDigest=$messageDigest )"
    }

}
