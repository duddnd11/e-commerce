package kr.hhplus.be.server.infrastructure.coupon;

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
		return jpaRepository.findById(userCouponId).orElseThrow();
	}

	@Override
	public UserCoupon findByUserId(Long userId) {
		return jpaRepository.findByUserId(userId); 
	}

}
