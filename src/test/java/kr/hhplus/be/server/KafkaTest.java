package kr.hhplus.be.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.sql.exec.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import io.netty.handler.timeout.TimeoutException;

@SpringBootTest
@Testcontainers 
@ActiveProfiles("test")
public class KafkaTest{
	private static final String TEST_TOPIC = "test-topic";
    private static final AtomicReference<String> receivedMessage = new AtomicReference<>();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @KafkaListener(topics = TEST_TOPIC, groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        receivedMessage.set(record.value());
    }
    
    @BeforeAll
    static void beforeAll() {
    	System.out.println("MYSQL_CONTAINER running: " + TestcontainersConfiguration.MYSQL_CONTAINER.isRunning());
    	System.out.println("REDIS_CONTAINER running: " + TestcontainersConfiguration.REDIS_CONTAINER.isRunning());
        System.out.println("KafkaContainer running: " + TestcontainersConfiguration.KAFKA_CONTAINER.isRunning());
        System.out.println("Kafka bootstrap: " + TestcontainersConfiguration.KAFKA_CONTAINER.getBootstrapServers());
    }
    
    @Test
    void kafkaTest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String testMessage = "Kafka Test";

        // when
        kafkaTemplate.send(TEST_TOPIC, testMessage);

        // then
        Awaitility.await()
                .pollInterval(300, TimeUnit.MILLISECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    System.out.println("Checking received message...");
                    assertThat(receivedMessage.get()).isEqualTo(testMessage);
                });
    }
}
