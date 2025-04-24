package kr.hhplus.be.server.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderAction {
	private long orderId;
	
	public static OrderAction of(long orderId) {
		OrderAction orderAction = new OrderAction(orderId);
		return orderAction;
	}
}
