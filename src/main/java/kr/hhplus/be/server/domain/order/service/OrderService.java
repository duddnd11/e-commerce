package kr.hhplus.be.server.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.DataPlatform;
import kr.hhplus.be.server.domain.order.dto.OrderAction;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRedisRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final OrderRedisRepository orderRedisRepository;
	private final DataPlatform dataPlatform;
	
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
	
	public void increaseProductScore(long orderId) {
		// redis rank score 증가
		List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
		for(OrderDetail orderDetail : orderDetails) {
			orderRedisRepository.increaseProductScore(orderDetail);
		}
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
	
	@Cacheable(value="topSellingProduct", key="'topSellingProduct'")
	public List<TopSellingProduct> topSellingProduct(LocalDateTime fromDate){
		return orderRedisRepository.productRanking(fromDate);
	}
	
	@Scheduled(cron = "0 0 0 * * *")
	@CacheEvict(value = "topSellingProduct", key = "'topSellingProduct'")
	public void topSellingProductEvict() {
		log.info("인기 상품 캐시 제거");
	}
	
	public boolean sendDataPlatform(long orderId) {
		log.info("데이터 플랫폼 전송");
		return dataPlatform.send(orderId);
	}
}
