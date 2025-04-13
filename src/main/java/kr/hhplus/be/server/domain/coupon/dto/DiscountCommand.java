package kr.hhplus.be.server.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscountCommand {
	private long couponId;
	private int totalPrice;
}
