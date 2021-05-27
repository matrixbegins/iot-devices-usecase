package hat.streaming.devices.modules.dbrepos

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.springframework.data.repository.CrudRepository

interface ClusterSignalRepo: CrudRepository<IOTDeviceSignal, Long>