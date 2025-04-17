package kr.hhplus.be.server.domain.order.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.dto.OrderAction;
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
	
	@Transactional
	public Order createOrder(OrderCommand orderCommand) {
		if (orderCommand.getOrderDetailCommands() == null) throw new IllegalArgumentException("주문 상품 목록이 없습니다.");
		
		int totalPrice = orderCommand.getOrderDetailCommands().stream()
		        .mapToInt(c -> c.getPrice() * c.getQuantity())
		        .sum();
		
		Order order = new Order(orderCommand.getUserId(), totalPrice);
		orderRepository.save(order);
		
		for(OrderDetailCommand orderDetailCommand : orderCommand.getOrderDetailCommands()) {
			OrderDetail orderDetail = new OrderDetail(order.getId(), orderDetailCommand);
			orderDetailRepository.save(orderDetail);
		}
		
		return order;
	}

	public void orderSuccess(OrderAction orderAction) {
		Order order = orderRepository.findById(orderAction.getOrderId());
		order.success();
	}
	
	public Order discount(OrderDiscount orderDiscount) {
		Order order = orderRepository.findById(orderDiscount.getOrderId());
		order.discount(orderDiscount);
		return order;
	}

	public OrderResult cancel(OrderAction orderAction) {
		Order order = orderRepository.findById(orderAction.getOrderId());
		order.cancel();
		return OrderResult.of(order.getId(), order.getUserCouponId(), orderDetailRepository.findByOrderId(order.getId()));
	}
}
