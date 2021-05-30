package hat.streaming.devices.modules.service

import hat.streaming.devices.modules.dbrepos.preparedstmt.IOTDeviceSignalPreparedStatementSetter
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class ClusterDeviceDBService(private val jdbcTemplate: JdbcTemplate? = null) {

    val logger: Logger = LoggerFactory.getLogger(ClusterDeviceDBService::class.java)

    suspend fun saveClusterDeviceSignals(signals: List<IOTDeviceSignal>) {
        // save array of this data into db
        val insertSQL = """ INSERT INTO devices.device_cluster_metric_tracker 
            | (device_id, signal_type, signal_unit, signal_value, device_cluster, org_id, facility_id, device_timestamp, 
            | reported_year, reported_month, reported_day, reported_hour, reported_minute, reported_sec, created_at)
            | VALUES (?,    ?,          ?,              ?,              ?,         ?,           ?,      to_timestamp(?::decimal/1000000),            
            | date_part('year', to_timestamp(?::decimal/1000000)) , date_part('month', to_timestamp(?::decimal/1000000)),              
            | date_part('day', to_timestamp(?::decimal/1000000)), date_part('hour', to_timestamp(?::decimal/1000000)),
            | date_part('minute', to_timestamp(?::decimal/1000000)), date_part('second', to_timestamp(?::decimal/1000000)), now() ) ON CONFLICT DO NOTHING;
        """.trimMargin()

        runBatchInsert(signals, insertSQL)

        logger.debug("batch Insert complete....")
    }

    private suspend fun runBatchInsert(signals: List<IOTDeviceSignal>, sql: String): Job {
        logger.info("Processing batch insert of size = {} ", signals.size)
        return coroutineScope {
            launch(Dispatchers.Default) {
                jdbcTemplate!!.batchUpdate(sql, IOTDeviceSignalPreparedStatementSetter(signals))
                logger.debug("batch Insert inside co routine ....")
            }
        }
    }
}

