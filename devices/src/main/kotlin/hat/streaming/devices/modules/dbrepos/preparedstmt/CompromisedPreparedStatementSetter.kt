package hat.streaming.devices.modules.dbrepos.preparedstmt

import hat.streaming.devices.modules.dto.BaseIOTSignal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import java.sql.PreparedStatement
import java.sql.SQLException


class CompromisedPreparedStatementSetter(private val signals: List<BaseIOTSignal>): BatchPreparedStatementSetter {

    val logger: Logger = LoggerFactory.getLogger(CompromisedPreparedStatementSetter::class.java)

    override fun setValues(stmt: PreparedStatement, idx: Int) {
        // (device_id, signal_type, signal_value, message_digest,  device_timestamp,
        // reported_year, reported_month, reported_day, reported_hour, reported_minute, reported_sec)
        val signal: BaseIOTSignal =  signals[idx]
        try {
            with(signal){
                stmt.setString(1, deviceId)
                stmt.setString(2, signalType)
                stmt.setDouble(3, signalValue)
                stmt.setString(4, messageDigest)
                stmt.setLong(5, timestamp)
                stmt.setLong(6, timestamp)
                stmt.setLong(7, timestamp)
                stmt.setLong(8, timestamp)
                stmt.setLong(9, timestamp)
                stmt.setLong(10, timestamp)
                stmt.setLong(11, timestamp)

            }
        }
        catch (ex: SQLException){
            logger.error("Error in prepared statements. ", ex)
        }
    }

    override fun getBatchSize(): Int = signals.size

}