package kr.hhplus.be.server.application;

import java.util.List;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderDetailCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCriteria {
	private long userId;
	private long userCouponId;
	private List<OrderDetailCriteria> orderDetails;
	
	public OrderCommand toOrderCommand(List<OrderDetailCommand> orderDetailCommands) {
		OrderCommand orderCommand = new OrderCommand(this.userId, orderDetailCommands);
		return orderCommand;
	}
}
