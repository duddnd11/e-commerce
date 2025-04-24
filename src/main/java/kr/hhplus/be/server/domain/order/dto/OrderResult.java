package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResult {
	private Long orderId;
	private Long userCouponId;
	private List<OrderDetail> orderDetails; 
	
	public static OrderResult of(Long orderId, Long userCouponId, List<OrderDetail> orderDetails) {
		OrderResult orderResult = new OrderResult(orderId, userCouponId, orderDetails);
		return orderResult;
	}
}
