package hat.streaming.devices


import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
class DevicesApplication

fun main(args: Array<String>) {
	val logger = LoggerFactory.getLogger(DevicesApplication::class.java)
	logger.info("starting app.....................................................")
	runApplication<DevicesApplication>(*args)
}
