package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private long orderId;
	private long totalPrice;
	private long finalPrice;
	
	public static OrderResponse from(Order order) {
		OrderResponse orderResponse = new OrderResponse();
		orderResponse.orderId = order.getId();
		orderResponse.totalPrice = order.getTotalPrice();
		orderResponse.finalPrice = order.getFinalPrice();
		return orderResponse;
	}
}
