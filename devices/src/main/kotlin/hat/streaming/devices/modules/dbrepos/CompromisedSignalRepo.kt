package hat.streaming.devices.modules.dbrepos

import hat.streaming.devices.modules.dto.BaseIOTSignal
import org.springframework.data.repository.CrudRepository

interface CompromisedSignalRepo: CrudRepository<BaseIOTSignal, Long> {}