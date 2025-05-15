package kr.hhplus.be.server.domain.coupon.repository;

import java.util.List;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

public interface CouponRepository {
	Coupon save(Coupon coupon);
	Coupon findById(long couponId);
	Coupon findByIdForUpdate(long couponId);
	List<Coupon> findExpire();
}
