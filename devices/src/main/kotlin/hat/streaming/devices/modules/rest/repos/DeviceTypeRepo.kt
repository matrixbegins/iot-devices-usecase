package hat.streaming.devices.modules.rest.repos


import hat.streaming.devices.modules.rest.models.DeviceTypes
import org.springframework.data.repository.CrudRepository

interface DeviceTypeRepo : CrudRepository<DeviceTypes, Long>