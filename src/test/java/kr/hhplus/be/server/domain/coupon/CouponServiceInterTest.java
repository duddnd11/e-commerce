package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;

@SpringBootTest
@Testcontainers
public class CouponServiceInterTest {

	@Autowired
	CouponRepository couponRepository;
	
	@Autowired
	CouponService couponService;
	
	@Test
	@DisplayName("쿠폰 발급 테스트")
	void issue() {
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		
		couponService.issue(CouponCommand.of(1L, coupon.getId()));

		Coupon resultCoupon = couponRepository.findById(coupon.getId());
		assertThat(resultCoupon.getIssuedQuantity()).isEqualTo(1);
	}
	
	/**
	 * Lock 적용 전 100개 스레드 동시 요청 결과 34 반환
	 * Lock 적용 후 100개 스레드 동시 요청 결과 100 반환
	 * @throws InterruptedException
	 */
	@Test
	@DisplayName("쿠폰 발급 동시성 테스트")
	void issueConcurrent() throws InterruptedException {
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		
		int numberOfThread = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		
		// when
		for (int i = 0; i < numberOfThread; i++) {
		    executorService.execute(() -> {
		        try {
		            barrier.await();
		            couponService.issue(CouponCommand.of(1L, coupon.getId()));
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            countDownLatch.countDown();
		        }
		    });
		}
		
		countDownLatch.await();
		executorService.shutdown();
		
		Coupon resultCoupon = couponRepository.findById(coupon.getId());
		assertThat(resultCoupon.getIssuedQuantity()).isEqualTo(100);
		assertThat(resultCoupon.getStatus()).isEqualTo(CouponStatus.SOLD_OUT);
	}
}
