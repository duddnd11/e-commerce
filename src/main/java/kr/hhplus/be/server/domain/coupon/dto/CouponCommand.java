package kr.hhplus.be.server.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCommand {
	private long userId;
	private long couponId;
	
	public static CouponCommand of(long userId, long couponId) {
		CouponCommand couponCommand = new CouponCommand(userId, couponId);
		return couponCommand;
	}
}
