package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Testcontainers
public class CouponServiceInterTest {

	@Autowired
	CouponRepository couponRepository;
	
	@Autowired
	UserCouponRepository userCouponRepository;
	
	@Autowired
	EntityManager em;
	
	@Autowired
	CouponService couponService;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Test
	@DisplayName("쿠폰 발급 테스트")
	void issue() {
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		redisTemplate.delete("coupon:"+coupon.getId());
		redisTemplate.delete("coupon:stock:"+coupon.getId());
		String key = "coupon:stock:" + coupon.getId();
	    for (int i = 0; i < 100; i++) {
	        redisTemplate.opsForList().rightPush(key, "coupon");
	    }
		
		couponService.issue(CouponCommand.of(1L, coupon.getId()));

		Long issuedCount = redisTemplate.opsForHash().size("coupon:"+coupon.getId());
		Long couponStock = redisTemplate.opsForList().size("coupon:stock:" + coupon.getId());

		assertThat(issuedCount).isEqualTo(1);
		assertThat(couponStock).isEqualTo(99);
		redisTemplate.delete("coupon:"+coupon.getId());
		redisTemplate.delete("coupon:stock:"+coupon.getId());
	}
	
	/**
	 * Lock 적용 전 100개 스레드 동시 요청 결과 34 반환
	 * Lock 적용 후 100개 스레드 동시 요청 결과 100 반환
	 * 스레드 100 -> 105개 수정 = 100개 성공
	 * @throws InterruptedException
	 */
	@Test
	@DisplayName("쿠폰 발급 동시성 테스트")
	void issueConcurrent() throws InterruptedException {
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		redisTemplate.delete("coupon:stock:"+coupon.getId());
		redisTemplate.delete("coupon:"+coupon.getId());
		String key = "coupon:stock:" + coupon.getId();
	    for (int i = 0; i < 100; i++) {
	        redisTemplate.opsForList().rightPush(key, "coupon");
	    }
		
		int numberOfThread = 105;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		
		// when
		for (int i = 0; i < numberOfThread; i++) {
			final long index = i;
		    executorService.execute(() -> {
		        try {
		            barrier.await();
		            couponService.issue(CouponCommand.of(index , coupon.getId()));
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            countDownLatch.countDown();
		        }
		    });
		}
		
		countDownLatch.await();
		executorService.shutdown();
		
		Long issuedCount = redisTemplate.opsForHash().size("coupon:"+coupon.getId());
		Long couponStock = redisTemplate.opsForList().size("coupon:stock:" + coupon.getId());

		assertThat(issuedCount).isEqualTo(100);
		assertThat(couponStock).isEqualTo(0);
		redisTemplate.delete("coupon:"+coupon.getId());
		redisTemplate.delete("coupon:stock:"+coupon.getId());
	}
	
	@Test
	@DisplayName("쿠폰 만료")
	@Transactional
	void expire() {
		Coupon coupon = new Coupon("쿠폰", CouponType.PRICE, 1000, 100);
		coupon.setExpireAt(-1);
		couponRepository.save(coupon);
		
		UserCoupon userCoupon = new UserCoupon(1L, coupon.getId());
		userCouponRepository.save(userCoupon);
		
		redisTemplate.opsForHash().putIfAbsent("coupon:"+coupon.getId(), userCoupon.getUserId(), 1);
		
		couponService.expire();
		
		Long size = redisTemplate.opsForHash().size("coupon:"+coupon.getId());
		assertThat(size).isEqualTo(0L);
	}
}
