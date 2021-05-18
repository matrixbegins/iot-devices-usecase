package hat.streaming.devices.modules.consumers.transformers

import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.springframework.stereotype.Component

@Component
class MetricConvertor {

    fun convertTemperature(signal: IOTDeviceSignal): IOTDeviceSignal{
        // assuming Standard Measuring Unit of temperature is Celsius.
        val newValue = when(signal.signalUnit?.toUpperCase()) {
                    "C" -> signal.signalValue
                    "K" -> signal.signalValue - 273.15
                    "F" -> (5/9) * (signal.signalValue - 32)
                    else -> signal.signalValue
            }
        // as we are converting measurements into Celsius hence change the signal type as well.
        signal.signalUnit = "C"
        signal.signalValue = newValue
        return signal
    }

    fun convertPressure(signal: IOTDeviceSignal): IOTDeviceSignal{
        // assuming our standard measuring unit for pressure is PSI
        val newValue = when(signal.signalUnit?.toUpperCase()) {
            "PSI" -> signal.signalValue
            "ATM" -> signal.signalValue * 14.696
            "PA" -> signal.signalValue / 6895
            else -> signal.signalValue
        }
        // as we are converting measurements into PSI hence change the signal type as well.
        signal.signalUnit = "PSI"
        signal.signalValue = newValue
        return signal
    }

    fun convertRadiation(signal: IOTDeviceSignal): IOTDeviceSignal{
        return signal
    }

    fun convertSignalMetric(signal: IOTDeviceSignal): IOTDeviceSignal{
        return when(signal.signalType?.toLowerCase()) {
                "temperature" -> convertTemperature(signal)
                "pressure" -> convertPressure(signal)
                "radiation" ->  convertRadiation(signal)
                else -> signal
            }
    }

}