package kr.hhplus.be.server.domain.order.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
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
