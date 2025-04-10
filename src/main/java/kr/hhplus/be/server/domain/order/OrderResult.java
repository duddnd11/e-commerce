package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResult {
	private long orderId;
	
	public static OrderResult of(long orderId) {
		OrderResult orderSuccess = new OrderResult(orderId);
		return orderSuccess;
	}
}
