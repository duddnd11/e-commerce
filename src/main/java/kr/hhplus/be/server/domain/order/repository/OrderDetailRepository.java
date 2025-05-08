package kr.hhplus.be.server.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;

public interface OrderDetailRepository {
	OrderDetail save(OrderDetail orderDetail);
	List<OrderDetail> findByOrderId(Long orderId);
	List<TopSellingProduct> findTopSellingProducts(LocalDateTime fromDate);
}
