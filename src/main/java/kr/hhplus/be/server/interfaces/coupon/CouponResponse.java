package kr.hhplus.be.server.interfaces.coupon;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.domain.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
	private long couponId;
	
	private String name;
	
	private CouponType type;
	
	private int value;
}
