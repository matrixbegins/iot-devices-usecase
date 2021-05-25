package hat.streaming.devices.common.configuration.kafka

import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*


@EnableKafka
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@ConfigurationProperties("spring.kafka")
@ConstructorBinding
class KafkaProducerConfig(private val bootstrapServers: String){

    private fun getCommonProducerProps() : MutableMap<String, Any> {
        var config: MutableMap<String, Any> = HashMap()
        config["bootstrap.servers"] = bootstrapServers
        config["acks"] = "1"
        config["retries"] = "1"
        config["compression.type"] = "gzip"
        config["partitioner.class"] = "org.apache.kafka.clients.producer.RoundRobinPartitioner"

        return config
    }

    @Bean
    fun stringMessageProducerFactory(): ProducerFactory<String, String> {
        val props: MutableMap<String, Any> = getCommonProducerProps()
        props["bootstrap.servers"] = bootstrapServers
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        props["client.id"] = "kotlin_app_" + UUID.randomUUID().toString()

        return DefaultKafkaProducerFactory<String, String>(props)
    }

    @Bean
    fun stringProducerTemplate(): KafkaTemplate<String, String> = KafkaTemplate(stringMessageProducerFactory())

    @Bean
    fun baseIOTProducerFactory(): ProducerFactory<String, BaseIOTSignal> {
        val props: MutableMap<String, Any> = getCommonProducerProps()
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = JsonSerializer::class.java
        props["client.id"] = "kotlin_baseiot_" + UUID.randomUUID().toString()

        return DefaultKafkaProducerFactory<String, BaseIOTSignal>(props)
    }

    @Bean
    fun baseIOTProducerTemplate(): KafkaTemplate<String, BaseIOTSignal> = KafkaTemplate(baseIOTProducerFactory())

    @Bean
    fun iotSignalProducerFactory(): ProducerFactory<String, IOTDeviceSignal> {
        val props: MutableMap<String, Any> = getCommonProducerProps()
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = JsonSerializer::class.java
        props["client.id"] = "kotlin_iotdevice_" + UUID.randomUUID().toString()

        return DefaultKafkaProducerFactory<String, IOTDeviceSignal>(props)
    }

    @Bean
    fun iotSignalProducerTemplate(): KafkaTemplate<String, IOTDeviceSignal> = KafkaTemplate(iotSignalProducerFactory())

}