package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository{
	private final UserCouponJpaRepository jpaRepository;
	
	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		return jpaRepository.save(userCoupon);
	}

	@Override
	public UserCoupon findById(Long userCouponId) {
		return jpaRepository.findById(userCouponId).orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다."));
	}

	@Override
	public UserCoupon findByUserId(Long userId) {
		return jpaRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다.")); 
	}

	@Override
	public UserCoupon findByIdForUpdate(Long userCouponId) {
		return jpaRepository.findByIdForUpdate(userCouponId).orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다."));
	}

	@Override
	public List<UserCoupon> findAllByUserId(Long userId) {
		return jpaRepository.findAllByUserId(userId);
	}

	@Override
	public void expire() {
		jpaRepository.expire();
	}

	@Override
	public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
		return jpaRepository.findByUserIdAndCouponId(userId, couponId);
	}

}
