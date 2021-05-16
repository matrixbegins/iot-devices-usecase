package hat.streaming.devices.modules.rest.repos

import hat.streaming.devices.modules.dto.DeviceInfo
import org.springframework.data.repository.CrudRepository
import java.util.*

interface DeviceInfoRepo : CrudRepository<DeviceInfo, Long>{

    fun findByDeviceId(deviceId: String): Optional<DeviceInfo>

    fun findByOrgIdAndFacilityId(orgId: String, facilityId: String): Iterable<DeviceInfo>
}
