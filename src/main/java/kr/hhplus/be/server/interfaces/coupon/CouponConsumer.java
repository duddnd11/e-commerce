package kr.hhplus.be.server.interfaces.coupon;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {
	private final CouponService couponService;
	
	@KafkaListener(topics = "coupon-issue", groupId = "coupon")
	public void couponIssue(@Payload CouponCommand couponCommand) {
		couponService.saveUserCoupon(couponCommand);
	}
}
