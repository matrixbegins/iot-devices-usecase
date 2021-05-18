package hat.streaming.devices.common.configuration.kafka

import hat.streaming.devices.modules.dto.BaseIOTSignal
import hat.streaming.devices.modules.dto.IOTDeviceSignal
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.StringOrBytesSerializer
import org.springframework.stereotype.Component


@EnableKafka
@Component
class KafkaConsumerConfig() {

    @Value("\${spring.kafka.bootstrap-servers:}")
    val bootstrapServers: String = ""

    private fun getCommonConsumerProps() : MutableMap<String, Any> {
        var config: MutableMap<String, Any> = HashMap()
        config["bootstrap.servers"] = bootstrapServers
        config["max.poll.records"] = "10"
        config["enable.auto.commit"] = true
        config["auto.offset.reset"] = "earliest"
        config["partition.assignment.strategy"] = "org.apache.kafka.clients.consumer.RoundRobinAssignor"

        return config
    }

    @Bean
    fun entryKeyValueTopicConsumerFactory(): ConsumerFactory<String, String> {
        var config: MutableMap<String, Any> = getCommonConsumerProps()
        config["key.deserializer"] = StringDeserializer::class.java
        config["value.deserializer"] = StringDeserializer::class.java
        config["group.id"] = "entry_topic_key_value_transformer_consumer"

        return DefaultKafkaConsumerFactory(config, StringDeserializer(), StringDeserializer())
    }

    @Bean
    fun keyValueEntryTopicContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = entryKeyValueTopicConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH;
        factory.isBatchListener = true
        factory.setConcurrency(1);  // run 4 concurrent instances of the factory. That means one group will have 4 consumers
        return factory
    }

    @Bean
    fun entryJsonTopicConsumerFactory(): ConsumerFactory<String?, BaseIOTSignal?>? { /// wrong data type
        val config: MutableMap<String, Any> = getCommonConsumerProps()

        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS] = StringOrBytesSerializer::class.java
        config[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] =  JsonDeserializer::class.java
        config[JsonDeserializer.VALUE_DEFAULT_TYPE] = "hat.streaming.devices.modules.dto.BaseIOTSignal"
        config[JsonDeserializer.KEY_DEFAULT_TYPE] = "kotlin.String"

        config["group.id"] = "entry_topic_json_transformer_consumer"

        return DefaultKafkaConsumerFactory(config, StringDeserializer(),
            JsonDeserializer<BaseIOTSignal>(BaseIOTSignal::class.java).ignoreTypeHeaders() )
    }

    @Bean
    fun jsonEntryTopicContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, BaseIOTSignal> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, BaseIOTSignal>()
        factory.consumerFactory = entryJsonTopicConsumerFactory()
        factory.isBatchListener = true
        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH
        factory.setConcurrency(1);  // run 4 concurrent instances of the factory. That means one group will have 4 consumers
        return factory
    }

    @Bean
    fun deviceClusterTopicConsumerFactory(): ConsumerFactory<String?, IOTDeviceSignal?>? {
        val config: MutableMap<String, Any> = getCommonConsumerProps()

        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS] = StringOrBytesSerializer::class.java
        config[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] =  JsonDeserializer::class.java
        config[JsonDeserializer.VALUE_DEFAULT_TYPE] = "hat.streaming.devices.modules.dto.IOTDeviceSignal"
        config[JsonDeserializer.KEY_DEFAULT_TYPE] = "kotlin.String"

        config["group.id"] = "device_cluster_data_db_dump_consumer"

        return DefaultKafkaConsumerFactory(config, StringDeserializer(),
            JsonDeserializer<IOTDeviceSignal>(IOTDeviceSignal::class.java).ignoreTypeHeaders() )
    }

    @Bean
    fun deviceClusterContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, IOTDeviceSignal> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, IOTDeviceSignal>()
        factory.consumerFactory = deviceClusterTopicConsumerFactory()
        factory.isBatchListener = true
        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH
        factory.setConcurrency(1);  // run 4 concurrent instances of the factory. That means one group will have 4 consumers
        return factory
    }

    @Bean
    fun allSignalTypeTopicConsumerFactory(): ConsumerFactory<String?, IOTDeviceSignal?>? {
        val config: MutableMap<String, Any> = getCommonConsumerProps()

        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS] = StringOrBytesSerializer::class.java
        config[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] =  JsonDeserializer::class.java
        config[JsonDeserializer.VALUE_DEFAULT_TYPE] = "hat.streaming.devices.modules.dto.IOTDeviceSignal"
        config[JsonDeserializer.KEY_DEFAULT_TYPE] = "kotlin.String"

        config["max.poll.records"] = "1000"
        config["group.id"] = "all_signal_type_data_db_dump_consumer"

        return DefaultKafkaConsumerFactory(config, StringDeserializer(),
            JsonDeserializer<IOTDeviceSignal>(IOTDeviceSignal::class.java).ignoreTypeHeaders() )
    }

    @Bean
    fun allSignalTypeContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, IOTDeviceSignal> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, IOTDeviceSignal>()
        factory.consumerFactory = allSignalTypeTopicConsumerFactory()
        factory.isBatchListener = true
        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH
        factory.setConcurrency(1);  // run 4 concurrent instances of the factory. That means one group will have 4 consumers
        return factory
    }

}