package hat.streaming.devices.modules.dbrepos

import hat.streaming.devices.modules.dto.BaseIOTSignal
import org.springframework.data.repository.CrudRepository

interface FaultySignalRepo: CrudRepository<BaseIOTSignal, Long>