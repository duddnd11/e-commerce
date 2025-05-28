package kr.hhplus.be.server;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Testcontainers
public class TestcontainersConfiguration {

    @Container
    public static MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
        .withDatabaseName("hhplus")
        .withUsername("test")
        .withPassword("test");

    @Container
    public static GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2.4"))
        .withExposedPorts(6379);

    @Container
    public static KafkaContainer KAFKA_CONTAINER  = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest").asCompatibleSubstituteFor("apache/kafka"));
    

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", () -> MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);

        // Redis
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }
}
