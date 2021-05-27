package hat.streaming.devices.modules.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger
import java.security.MessageDigest

open class BaseIOTSignal (
    var signalValue: Double,
    var deviceId:  String? = null,
    var timestamp: Long,

    @JsonProperty("digest")
    var messageDigest: String? = null,
    var signalType: String? = null
) {
    private fun getSHA512(input:String):String{
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(input.toByteArray())

        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)

        // Convert message digest into hex value
        var hashText: String = no.toString(16)

        // Add preceding 0s to make it 32 bit
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }

        // return the HashText
        return hashText
    }

    fun validateMessageDigest(): Boolean {
        val payload = "${this.deviceId}|${this.signalValue}|${this.timestamp}"
        return this.messageDigest.equals(getSHA512(payload))
    }

    override fun toString(): String {
        return "BaseIOTSignal(signalValue=$signalValue, deviceId=$deviceId, timestamp=$timestamp, messageDigest=$messageDigest)"
    }

}
