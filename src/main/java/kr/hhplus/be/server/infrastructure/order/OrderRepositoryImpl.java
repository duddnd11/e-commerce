package kr.hhplus.be.server.infrastructure.order;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository{
	private final OrderJpaRepository jpaRepository;

	@Override
	public Order save(Order order) {
		return jpaRepository.save(order);
	}

	@Override
	public Order findById(long orderId) {
		return jpaRepository.findById(orderId).orElseThrow();
	}
}
