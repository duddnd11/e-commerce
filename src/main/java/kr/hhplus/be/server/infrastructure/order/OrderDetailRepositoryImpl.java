package kr.hhplus.be.server.infrastructure.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@Override
	public List<TopSellingProduct> findTopSellingProducts(LocalDateTime fromDate) {
		LocalDate to = LocalDate.now();
		LocalDateTime today = to.atStartOfDay();
		return jpaRepository.findTopSellingProducts(fromDate, today);
	}
}
