package hat.streaming.devices.modules.dbrepos.preparedstmt

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import java.sql.PreparedStatement
import java.sql.SQLException


class IOTDeviceSignalPreparedStatementSetter(private val signals: List<IOTDeviceSignal>): BatchPreparedStatementSetter {

    val logger: Logger = LoggerFactory.getLogger(IOTDeviceSignalPreparedStatementSetter::class.java)

    override fun setValues(stmt: PreparedStatement, idx: Int) {
        // | (device_id, signal_type, signal_unit, signal_value, device_cluster, org_id, facility_id, device_timestamp,
        //  | reported_year, reported_month, reported_day, reported_hour, reported_minute, reported_sec)
        var signal: IOTDeviceSignal =  signals[idx]
        try {
            with(signal){
                stmt.setString(1, deviceId)
                stmt.setString(2, signalType)
                stmt.setString(3, signalUnit)
                stmt.setDouble(4, signalValue)
                stmt.setString(5, deviceCluster)
                stmt.setString(6, orgId)
                stmt.setString(7, facilityId)

                stmt.setLong(8, timestamp)
                stmt.setLong(9, timestamp)
                stmt.setLong(10, timestamp)
                stmt.setLong(11, timestamp)
                stmt.setLong(12, timestamp)
                stmt.setLong(13, timestamp)
                stmt.setLong(14, timestamp)

            }
            logger.info("Processing batch insert of size = {} ", signals.size)
        }
        catch (ex: SQLException){
            logger.error("Error in prepared statements. ", ex)
        }

    }

    override fun getBatchSize(): Int = signals.size
}