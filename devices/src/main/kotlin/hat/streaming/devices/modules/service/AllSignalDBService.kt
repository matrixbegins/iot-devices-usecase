package hat.streaming.devices.modules.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class AllSignalDBService(private val jdbcTemplate: JdbcTemplate? = null) {

    val logger: Logger = LoggerFactory.getLogger(AllSignalDBService::class.java)


    fun getCreateSignalMetricTrackerTableSQL(signalType: String): String {
        val templateSql = "CREATE TABLE devices.##table_name##_metric_tracker AS SELECT * FROM devices.signal_type_metric_template;"
        return templateSql.replace("##table_name##", signalType)
    }

    fun getCreateSignalMetricTrackerIndexSQLs(signalType: String): String {

        val indexSql = """
                    CREATE INDEX ##table_name##_idx_cluster ON devices.##table_name## USING BTREE (device_cluster);
                    CREATE INDEX ##table_name##_idx_org_id ON devices.##table_name## USING BTREE (org_id);
                    CREATE INDEX ##table_name##_idx_facility_id ON devices.##table_name## USING BTREE (facility_id);
                    CREATE INDEX ##table_name##_idx_reported_year ON devices.##table_name## USING BTREE (reported_year);
                    CREATE INDEX ##table_name##_idx_reported_month ON devices.##table_name## USING BTREE (reported_month);
                    CREATE INDEX ##table_name##_idx_reported_day ON devices.##table_name## USING BTREE (reported_day);
                    CREATE INDEX ##table_name##_idx_reported_hour ON devices.##table_name## USING BTREE (reported_hour);
                    CREATE INDEX ##table_name##_idx_reported_minute ON devices.##table_name## USING BTREE (reported_minute);
                    CREATE INDEX ##table_name##_idx_reported_sec ON devices.##table_name## USING BTREE (reported_sec);
                """.trimIndent()

        return indexSql.replace("##table_name##", signalType)
    }




}