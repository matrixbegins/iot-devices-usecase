package hat.streaming.devices.modules.service

import hat.streaming.devices.modules.dbrepos.FaultySignalRepo
import hat.streaming.devices.modules.dbrepos.preparedstmt.BaseIOTSignalPreparedStatementSetter
import hat.streaming.devices.modules.dto.BaseIOTSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class FaultySignalService(private val jdbcTemplate: JdbcTemplate? = null, val faultySignalRepo: FaultySignalRepo) {

    val logger: Logger = LoggerFactory.getLogger(FaultySignalService::class.java)

    suspend fun saveSignals(signals: List<BaseIOTSignal>): Unit {
        // save array of this data into db
        val insertSQL = """ INSERT INTO devices.faulty_signals 
            | (device_id, signal_type, signal_value, device_timestamp, reported_year, reported_month, reported_day, reported_hour, reported_minute, reported_sec)
            | VALUES (?,    ?,          ?,    to_timestamp(? /1000),            
            | date_part('year', to_timestamp(? / 1000)) , date_part('month', to_timestamp(? / 1000)),              
            | date_part('day', to_timestamp(? / 1000)), date_part('hour', to_timestamp(? / 1000)),
            | date_part('minute', to_timestamp(? / 1000)), date_part('second', to_timestamp(? / 1000)) )
        """.trimMargin()

        runBatchInsert(signals, insertSQL)

        logger.info("batch Insert complete....")
    }

    private suspend fun runBatchInsert(signals: List<BaseIOTSignal>, sql: String): Job {
        return coroutineScope {
             launch(Dispatchers.Default) {
                jdbcTemplate!!.batchUpdate(sql, BaseIOTSignalPreparedStatementSetter(signals))
                 logger.info("batch Insert inside co routine ....")
            }
        }
    }

}

