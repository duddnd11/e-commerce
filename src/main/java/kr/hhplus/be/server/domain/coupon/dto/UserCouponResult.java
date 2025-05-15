package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponResult {
	private Long userId;
	private Long couponId;
	private UserCouponStatus status;
	
	public static UserCouponResult of(Long userId, Long couponId, UserCouponStatus status) {
		UserCouponResult userCouponResult = new UserCouponResult(userId, couponId, status);
		return userCouponResult;
	}
}
