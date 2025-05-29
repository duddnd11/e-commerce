package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventImpl implements CouponEvent{
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ObjectMapper objectMapper;
	
	@Override
	public void couponIssue(CouponCommand couponCommand) {
		try {
			log.info("coupon kafka event");
			String couponCommandJson = objectMapper.writeValueAsString(couponCommand);
			kafkaTemplate.send("coupon-issue", couponCommandJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
