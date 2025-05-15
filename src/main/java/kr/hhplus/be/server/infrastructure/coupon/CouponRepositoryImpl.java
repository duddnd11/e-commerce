package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;

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
		return jpaRepository.findById(couponId).orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
	}

	@Override
	public Coupon save(Coupon coupon) {
		return jpaRepository.save(coupon);
	}

	@Override
	public Coupon findByIdForUpdate(long couponId) {
		return jpaRepository.findByIdForUpdate(couponId).orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
	}

	@Override
	public List<Coupon> findExpire() {
		return jpaRepository.findExpire();
	}
}
