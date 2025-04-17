package kr.hhplus.be.server.infrastructure.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository{
	private final OrderDetailJpaRepository jpaRepository;
	
	@Override
	public OrderDetail save(OrderDetail orderDetail) {
		return jpaRepository.save(orderDetail);
	}

	@Override
	public List<OrderDetail> findByOrderId(Long orderId) {
		return jpaRepository.findByOrderId(orderId);
	}
}
