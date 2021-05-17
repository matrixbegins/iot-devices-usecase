package hat.streaming.devices.modules.consumers.transformers

import hat.streaming.devices.modules.dto.BaseIOTSignal
import org.springframework.stereotype.Component

@Component
class KeyValueMessageParser {

    fun parseKeyValueMessage(signalStr: String): BaseIOTSignal {

        // message format
        // "did|JWWMFAJQ8B|val|6.24|ts|1621176860178|dgst|f47d0c7733f36ce63c0"

        val result = signalStr.split("|")

        return BaseIOTSignal(deviceId = result[1], signalValue = result[3].toDouble(),
                            timestamp = result[5].toLong(), messageDigest = result[7])
    }

}