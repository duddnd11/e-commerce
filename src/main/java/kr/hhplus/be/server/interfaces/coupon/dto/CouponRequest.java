package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import lombok.Getter;

@Getter
public class CouponRequest {
	private long userId;
	private long couponId;
	
	public CouponCommand toCouponCommand() {
		return new CouponCommand(userId, couponId);
	}
}
