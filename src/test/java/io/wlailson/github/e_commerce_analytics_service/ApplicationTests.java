package io.wlailson.github.e_commerce_analytics_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class ApplicationTests {

	@Container
	@ServiceConnection
	static final KafkaContainer kafkaContainer =
			new KafkaContainer(DockerImageName.parse("apache/kafka-native:4.2.1"));

	@Container
	@ServiceConnection
	static final MongoDBContainer mongoDbContainer =
			new MongoDBContainer(DockerImageName.parse("mongo:8.2"));

	@Test
	void contextLoads() {
	}

}
