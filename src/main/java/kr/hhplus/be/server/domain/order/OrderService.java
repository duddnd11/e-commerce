package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	
	public Order createOrder(OrderCommand orderCommand) {
		Order order = new Order(orderCommand);
		orderRepository.save(order);
		
		for(OrderDetailCommand orderDetailCommand : orderCommand.getOrderDetailCommands()) {
			OrderDetail orderDetail = new OrderDetail(order.getId(), orderDetailCommand);
			orderDetailRepository.save(orderDetail);
		}
		
		return order;
	}

	public void orderSuccess(OrderResult orderResult) {
		Order order = orderRepository.findById(orderResult.getOrderId());
		order.success();
	}
	
	public Order discount(OrderDiscount orderDiscount) {
		Order order = orderRepository.findById(orderDiscount.getOrderId());
		order.discount(orderDiscount);
		return order;
	}

	public void cancel(OrderResult orderResult) {
		Order order = orderRepository.findById(orderResult.getOrderId());
		order.cancel();
	}
}
