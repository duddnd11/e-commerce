package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderDetailCriteria;
import lombok.Getter;

@Getter
public class OrderDetailRequest {
	private long productId;
	private int quantity;
	
	public OrderDetailCriteria toOrderDetailCriteria() {
		OrderDetailCriteria orderDetailCriteria = new OrderDetailCriteria(productId, quantity);
		return orderDetailCriteria; 
	}
}
