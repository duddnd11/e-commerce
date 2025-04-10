package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDiscount {
	private long orderId;
	private long userCouponId;
	private int discountValue;
}
