package kr.hhplus.be.server.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.DiscountCommand;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.OrderDiscount;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.DeductStockCommand;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;
	private final ProductService productService;
	private final CouponService couponService;
	
	public Order order(OrderCriteria orderCriteria) {
		// 재고 차감
		List<OrderDetailCommand> orderDetailCommands = new ArrayList<OrderDetailCommand>();
		for(OrderDetailCriteria orderDetailDto : orderCriteria.getOrderDetails()) {
			Product product = productService.deductStock(DeductStockCommand.of(orderDetailDto.getProductId(), orderDetailDto.getQuantity()));
			orderDetailCommands.add(OrderDetailCommand.of(orderDetailDto.getProductId(), orderDetailDto.getQuantity(), product.getPrice()));
		}
		
		// 주문
		Order order = orderService.createOrder(orderCriteria.toOrderCommand(orderDetailCommands));
		
		// 쿠폰 사용
		if(orderCriteria.getUserCouponId() > 0) {
			UserCoupon userCoupon = couponService.useCoupon(orderCriteria.getUserCouponId());
			// 할인금액 계산
			int discountValue = couponService.calDiscountValue(new DiscountCommand(userCoupon.getCouponId(), order.getTotalPrice()));
			order = orderService.discount(new OrderDiscount(order.getId(), orderCriteria.getUserCouponId(), discountValue));
		}
		
		return order;
	}
}
