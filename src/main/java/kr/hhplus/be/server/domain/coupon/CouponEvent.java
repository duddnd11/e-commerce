package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;

public interface CouponEvent {
	void couponIssue(CouponCommand couponCommand);
}
