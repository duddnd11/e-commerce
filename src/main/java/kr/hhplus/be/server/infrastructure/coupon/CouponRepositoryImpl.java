package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository{
	private final CouponJpaRepository jpaRepository;
	
	@Override
	public Coupon findById(long couponId) {
		return jpaRepository.findById(couponId).orElseThrow();
	}

	@Override
	public Coupon save(Coupon coupon) {
		return jpaRepository.save(coupon);
	}

}
