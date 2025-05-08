package kr.hhplus.be.server.infrastructure.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long>{
	List<OrderDetail> findByOrderId(Long orderId);
	
	@Query("SELECT new kr.hhplus.be.server.domain.order.dto.TopSellingProduct(od.productId, SUM(od.quantity)) " +
		       "FROM OrderDetail od " +
		       "JOIN Orders o ON od.orderId = o.id " +
		       "WHERE o.orderAt >= :fromDate " +
		       "GROUP BY od.productId " +
		       "ORDER BY SUM(od.quantity) DESC")
	List<TopSellingProduct> findTopSellingProducts(@Param("fromDate") LocalDateTime fromDate);

}
