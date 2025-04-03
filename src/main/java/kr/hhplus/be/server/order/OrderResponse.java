package kr.hhplus.be.server.order;

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
}
