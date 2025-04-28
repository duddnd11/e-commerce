package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	UserCoupon findById(Long userCouponId);
	UserCoupon findByUserId(Long userId);
	UserCoupon findByIdForUpdate(Long userCouponId);
}
