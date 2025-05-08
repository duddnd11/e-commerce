package kr.hhplus.be.server.application.payment.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponCommand;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.dto.OrderAction;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.dto.StockCommand;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRollbackFacade {
	private final ProductService productService;
	private final CouponService couponService;
	private final OrderService orderService;
	
	@Transactional
	public void rollBack(PaymentCriteria paymentCriteria) {
		OrderResult cancelOrderResult = orderService.cancel(OrderAction.of(paymentCriteria.getOrderId()));
		
		List<StockCommand> stockCommands = cancelOrderResult.getOrderDetails().stream()
			    .map(od -> StockCommand.of(od.getProductId(), od.getQuantity()))
			    .collect(Collectors.toList());
		productService.addStock(stockCommands);
		
		couponService.cancel(UserCouponCommand.of(cancelOrderResult.getUserCouponId()));
	}
}
