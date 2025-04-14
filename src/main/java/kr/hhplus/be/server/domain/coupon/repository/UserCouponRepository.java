package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.entity.User;

public interface UserCouponRepository {
	UserCoupon save(UserCoupon userCoupon);
	UserCoupon findById(Long userCouponId);
	UserCoupon findByUser(User user);
}
