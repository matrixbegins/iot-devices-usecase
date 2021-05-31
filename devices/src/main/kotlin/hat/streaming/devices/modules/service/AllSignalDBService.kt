package hat.streaming.devices.modules.service

import hat.streaming.devices.modules.dbrepos.preparedstmt.IOTDeviceSignalPreparedStatementSetter
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class AllSignalDBService(private val jdbcTemplate: JdbcTemplate? = null) {

    val logger: Logger = LoggerFactory.getLogger(AllSignalDBService::class.java)

    fun getCreateSignalMetricTrackerTableSQL(signalType: String): String {
        val templateSql = """
            CREATE TABLE IF NOT EXISTS devices.##table_name##_metric_tracker (
                device_id varchar(50) NOT NULL,
                signal_type varchar(50) NOT NULL,
                signal_unit varchar(50) NOT NULL,
                signal_value NUMERIC(10,4) NOT NULL,
                device_cluster varchar(50) NOT NULL,
                org_id varchar(50) NOT NULL,
                facility_id varchar(50) NOT NULL,
                device_timestamp timestamp NOT NULL,
                reported_year SMALLINT NOT NULL,
            	reported_month SMALLINT NOT NULL,
            	reported_day SMALLINT NOT NULL,
            	reported_hour SMALLINT NOT NULL,
            	reported_minute SMALLINT NOT NULL,
            	reported_sec SMALLINT NOT NULL,
                created_at timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
                PRIMARY KEY (device_id, created_at)
            );
        """.trimIndent()
        return templateSql.replace("##table_name##", signalType)
    }

    fun getCreateSignalMetricTrackerIndexSQLs(signalType: String): String {

        val indexSql = """
                    CREATE INDEX ##table_name##_idx_cluster ON devices.##table_name##_metric_tracker USING BTREE (device_cluster);
                    CREATE INDEX ##table_name##_idx_org_id ON devices.##table_name##_metric_tracker USING BTREE (org_id);
                    CREATE INDEX ##table_name##_idx_facility_id ON devices.##table_name##_metric_tracker USING BTREE (facility_id);
                    CREATE INDEX ##table_name##_idx_reported_year ON devices.##table_name##_metric_tracker USING BTREE (reported_year);
                    CREATE INDEX ##table_name##_idx_reported_month ON devices.##table_name##_metric_tracker USING BTREE (reported_month);
                    CREATE INDEX ##table_name##_idx_reported_day ON devices.##table_name##_metric_tracker USING BTREE (reported_day);
                    CREATE INDEX ##table_name##_idx_reported_hour ON devices.##table_name##_metric_tracker USING BTREE (reported_hour);
                    CREATE INDEX ##table_name##_idx_reported_minute ON devices.##table_name##_metric_tracker USING BTREE (reported_minute);
                    CREATE INDEX ##table_name##_idx_reported_sec ON devices.##table_name##_metric_tracker USING BTREE (reported_sec);
                """.trimIndent()

        return indexSql.replace("##table_name##", signalType)
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

    suspend fun saveMetricTrackerSignals( signalType: String, signals: List<IOTDeviceSignal>) {
        // save array of this data into db
        val insertTemplateSQL = """ INSERT INTO devices.##table_name##_metric_tracker
            | (device_id, signal_type, signal_unit, signal_value, device_cluster, org_id, facility_id, device_timestamp, 
            | reported_year, reported_month, reported_day, reported_hour, reported_minute, reported_sec, created_at)
            | VALUES (?,    ?,          ?,              ?,              ?,         ?,           ?,      to_timestamp(?::decimal/1000000),            
            | date_part('year', to_timestamp(?::decimal/1000000)) , date_part('month', to_timestamp(?::decimal/1000000)),              
            | date_part('day', to_timestamp(?::decimal/1000000)), date_part('hour', to_timestamp(?::decimal/1000000)),
            | date_part('minute', to_timestamp(?::decimal/1000000)), date_part('second', to_timestamp(?::decimal/1000000)), now() ) ON CONFLICT DO NOTHING;
        """.trimMargin()

        val insertSQL = insertTemplateSQL.replace("##table_name##", signalType)

        try {
            runBatchInsert(signals, insertSQL)
        }
        catch (ex: Exception){
            logger.error("PGSQL ERROR happened = {} ", ex::class.java.simpleName as String)
//            logger.error("PGSQL ERROR happened = {} ", ex::class.jvmName)
//            logger.error("PGSQL ERROR happened = {} ", ex::class.java.canonicalName)
//            logger.error("PGSQL ERROR happened = {} ", ex::class.java.packageName)
            logger.error("Error Details: ", ex)
            when(ex) {
                is BadSqlGrammarException -> {
                    // this is most likely table not found exception
                    createSignalMetricTrackerTable(signalType)
                    // table is created now re-run batch
                    runBatchInsert(signals, insertSQL)
                }
                is DuplicateKeyException -> return      // for testing this may cause a trouble.
                else -> throw ex
            }
        }

        logger.debug("batch Insert complete....")
    }

    fun createSignalMetricTrackerTable(signalType: String): Boolean {

        val createTableSQL = getCreateSignalMetricTrackerTableSQL(signalType)
        val indexSQL = getCreateSignalMetricTrackerIndexSQLs(signalType)

        logger.debug("creating table for Metric= {}", signalType )
        logger.debug("table SQL= {}", createTableSQL  )
        logger.debug("table indexes SQL= {}", indexSQL  )
        jdbcTemplate?.execute(createTableSQL)
        jdbcTemplate?.execute(indexSQL)
        createHyperTable(signalType)

        return true
    }

    fun createHyperTable(signalType: String): Boolean {
        try{
            val sql = "SELECT public.create_hypertable('devices.##table_name##_metric_tracker'::regclass, 'created_at'::name, if_not_exists => TRUE );"
            jdbcTemplate?.execute(sql.replace("##table_name##", signalType))
            logger.debug("hyper table created= {}", sql )
            return true

        }catch(ex: Exception) {
            logger.error("Error Creating hyper table: ", ex)
        }
        return false
    }

}