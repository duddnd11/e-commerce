package kr.hhplus.be.server.config.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponService couponService;

    // 매일 00:00:00 실행
    @Scheduled(cron = "0 0 0 * * *") 
    public void checkCouponExpiration() {
        couponService.expire();
    }
}

