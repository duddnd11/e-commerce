package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderSuccess {
	private long orderId;
	
	public static OrderSuccess of(long orderId) {
		OrderSuccess orderSuccess = new OrderSuccess(orderId);
		return orderSuccess;
	}
}
