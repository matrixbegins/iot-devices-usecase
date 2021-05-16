package hat.streaming.devices.modules.rest.models

import org.springframework.data.annotation.Id
import java.time.LocalDateTime


open class DeviceTypes(
    @Id var id: Long? = null,
    var type: String? = null,
    var description: String? = null,
    var threshold: Int? = null,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)