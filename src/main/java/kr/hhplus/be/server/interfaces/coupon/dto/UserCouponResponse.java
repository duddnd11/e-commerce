package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponResponse {
	private long userId;
	private long couponId;
	private UserCouponStatus status;
	
	public static UserCouponResponse from(long userId, long couponId, UserCouponStatus status) {
		UserCouponResponse userCouponResponse = new UserCouponResponse(userId, couponId, status);
		return userCouponResponse;
	}
}
