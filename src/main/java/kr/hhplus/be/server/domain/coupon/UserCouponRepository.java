package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.entity.User;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	UserCoupon findById(Long userCouponId);
	UserCoupon findByUser(User user);
}
