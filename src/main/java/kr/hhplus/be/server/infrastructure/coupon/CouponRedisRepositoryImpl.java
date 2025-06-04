package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponRedisRepositoryImpl implements CouponRedisRepository{
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public UserCouponResult issue(CouponCommand couponCommand) {
		String couponStockKey = "coupon:stock:"+couponCommand.getCouponId();
		String couponKey = "coupon:"+couponCommand.getCouponId();
		String userCouponKey = "user:coupon:"+couponCommand.getUserId();
		
		// 쿠폰 재고 검증
		Long couponStock = redisTemplate.opsForList().size(couponStockKey);
		if(couponStock == null || couponStock <=0) {
			return null;
		}
		
		Boolean issued = redisTemplate.opsForHash().putIfAbsent(couponKey, couponCommand.getUserId(), 1);
		if(Boolean.TRUE.equals(issued)) {
			log.info("issue:"+ couponCommand.getUserId() +":"+couponKey+":"+System.currentTimeMillis());
			// 선착순 쿠폰 발급
			String coupon = (String) redisTemplate.opsForList().leftPop(couponStockKey);
			
			// 쿠폰 없을 경우 실패
			if(coupon == null) {
				log.info("실패:"+couponCommand.getUserId());
				redisTemplate.opsForHash().delete(couponKey, couponCommand.getUserId());
				return null;
			}else {
				redisTemplate.opsForHash().putIfAbsent(userCouponKey, couponCommand.getCouponId(), 1);
			}
		}
		
		return UserCouponResult.of(couponCommand.getUserId(), couponCommand.getCouponId(), UserCouponStatus.AVAILABLE);
	}

	@Override
	public UserCouponResult use(CouponCommand couponCommand) {
		Long value = redisTemplate.opsForHash().delete("coupon:"+couponCommand.getCouponId(), couponCommand.getUserId());
		if(Long.valueOf(1).equals(value)) {
			redisTemplate.opsForHash().delete("user:coupon:"+couponCommand.getUserId(), couponCommand.getCouponId());
			return UserCouponResult.of(couponCommand.getUserId(), couponCommand.getCouponId(), UserCouponStatus.USED);
		}
		return null;
	}

	@Override
	public List<UserCouponResult> findUserCoupon(long userId) {
		Set<Object> userCouponSet = redisTemplate.opsForHash().keys("user:coupon:"+userId);
		return userCouponSet.stream()
				.map(userCoupon -> UserCouponResult.of(userId, Long.parseLong(userCoupon.toString()), UserCouponStatus.AVAILABLE))
				.collect(Collectors.toList());
	}

	@Override
	public void expire(long couponId) {
		redisTemplate.delete("coupon:"+couponId);
	}
}
