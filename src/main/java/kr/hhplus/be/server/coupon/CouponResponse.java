package kr.hhplus.be.server.coupon;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
	private long couponId;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private CouponType type;
	
	private int value;
}
