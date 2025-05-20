package kr.hhplus.be.server.application.order.dto;

import java.util.List;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCriteria {
	private long userId;
	private long couponId;
	private List<OrderDetailCriteria> orderDetails;
	
	public OrderCommand toOrderCommand(List<OrderDetailCommand> orderDetailCommands) {
		OrderCommand orderCommand = new OrderCommand(this.userId, orderDetailCommands);
		return orderCommand;
	}
}
