package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;

public interface OrderDetailRepository {
	OrderDetail save(OrderDetail orderDetail);
}
