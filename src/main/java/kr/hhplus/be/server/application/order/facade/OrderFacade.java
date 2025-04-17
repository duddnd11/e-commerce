package kr.hhplus.be.server.application.order.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.domain.coupon.dto.DiscountCommand;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.dto.StockCommand;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class OrderFacade {
	private final OrderService orderService;
	private final ProductService productService;
	private final CouponService couponService;
	
	@Transactional
	public Order order(OrderCriteria orderCriteria) {
		// 재고 차감
		List<StockCommand> stockCommands = orderCriteria.getOrderDetails().stream()
				.map(od -> StockCommand.of(od.getProductId(), od.getQuantity()))
				.collect(Collectors.toList());
		List<ProductResult> deductProducts = productService.deductStock(stockCommands);
		
		// 주문
		List<OrderDetailCommand> orderDetailCommands = deductProducts.stream()
				.map(dp -> OrderDetailCommand.of(dp.getProductId(), dp.getQuantity(), dp.getPrice()))
				.collect(Collectors.toList());
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
