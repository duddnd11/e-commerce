package kr.hhplus.be.server.domain.coupon.repository;

import java.util.List;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResult;

public interface CouponRedisRepository {
	UserCouponResult issue(CouponCommand couponCommand);
	UserCouponResult use(CouponCommand couponCommand);
	List<UserCouponResult> findUserCoupon(long userId);
	void expire(long couponId);
}
