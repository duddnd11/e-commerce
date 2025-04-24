package kr.hhplus.be.server.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponCommand {
	private Long userCouponId;
	
	public static UserCouponCommand of(Long userCouponId) {
		UserCouponCommand userCouponCommand = new UserCouponCommand(userCouponId);
		return userCouponCommand;
	}
}
