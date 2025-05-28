package kr.hhplus.be.server.infrastructure.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.order.DataPlatform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatFormClient implements DataPlatform{
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	@Override
	public void paymentDataPlatformSend(long orderId) {
		log.info("결제 데이터 전송");
		kafkaTemplate.send("send-payment", String.valueOf(orderId));
	}
	
}
