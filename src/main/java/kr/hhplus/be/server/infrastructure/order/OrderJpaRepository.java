package kr.hhplus.be.server.infrastructure.order;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long>{
}
