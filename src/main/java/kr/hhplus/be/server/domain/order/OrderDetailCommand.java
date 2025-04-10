package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailCommand {
	private long productId;
	private int quantity;
	private int price;
	
	public static OrderDetailCommand of(long productId, int quantity, int price) {
		OrderDetailCommand orderDetailCommand = new OrderDetailCommand();
		orderDetailCommand.productId = productId;
		orderDetailCommand.quantity = quantity;
		orderDetailCommand.price = price;
		return orderDetailCommand;
	}
}
