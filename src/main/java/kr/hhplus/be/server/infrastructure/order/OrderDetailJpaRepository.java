package kr.hhplus.be.server.infrastructure.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long>{
	List<OrderDetail> findByOrderId(Long orderId);
}
