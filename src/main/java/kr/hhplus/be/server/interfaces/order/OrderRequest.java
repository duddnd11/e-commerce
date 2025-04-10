package kr.hhplus.be.server.interfaces.order;

import java.util.List;
import java.util.stream.Collectors;

import kr.hhplus.be.server.application.OrderCriteria;
import kr.hhplus.be.server.application.OrderDetailCriteria;
import lombok.Getter;

@Getter
public class OrderRequest {
	private long userId;
	private long userCouponId;
	private List<OrderDetailRequest> orderDetails;
	
	public OrderCriteria toOrderCriteria() {
		List<OrderDetailCriteria> orderDetailCriterias = orderDetails.stream()
				.map(orderDetail -> orderDetail.toOrderDetailCriteria())
				.collect(Collectors.toList());
		
		OrderCriteria orderCriteria = new OrderCriteria(userId, userCouponId, orderDetailCriterias);
		return orderCriteria;
	}
}
