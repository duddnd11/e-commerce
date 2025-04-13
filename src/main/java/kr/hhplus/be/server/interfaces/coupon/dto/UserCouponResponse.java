package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponResponse {
	private long userCouponId;	

	private UserCouponStatus status;
	
	public static UserCouponResponse from(long userCouponId, UserCouponStatus status) {
		UserCouponResponse userCouponResponse = new UserCouponResponse(userCouponId, status);
		return userCouponResponse;
	}
}
