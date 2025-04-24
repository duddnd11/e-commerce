package kr.hhplus.be.server.application.payment.facade;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.domain.order.dto.OrderAction;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.user.dto.BalanceCommand;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacade {
	private final PaymentService paymentService;
	private final UserService userService;
	private final OrderService orderService;
	private final PaymentRollbackFacade paymentRollbackFacade;
	
	/*
	@Transactional
	public Payment payment(PaymentCriteria paymentCriteria) {
		try{
			// 유저 잔액 차감
			userService.useBalance(BalanceCommand.of(paymentCriteria.getUserId(), paymentCriteria.getAmount()));
			// 결제
			Payment payment = paymentService.createPayment(PaymentCommand.of(paymentCriteria.getOrderId()));
			
			// 주문 완료 처리
			orderService.orderSuccess(OrderAction.of(paymentCriteria.getOrderId()));
			return payment;
		}catch (IllegalArgumentException | ObjectOptimisticLockingFailureException e) {
			OrderResult cancelOrderResult = orderService.cancel(OrderAction.of(paymentCriteria.getOrderId()));
			
			List<StockCommand> stockCommands = cancelOrderResult.getOrderDetails().stream()
				    .map(od -> StockCommand.of(od.getProductId(), od.getQuantity()))
				    .collect(Collectors.toList());
			productService.addStock(stockCommands);
			
			couponService.cancel(UserCouponCommand.of(cancelOrderResult.getUserCouponId()));
			return null;
		}
	}
	 */
	
	public Payment payment(PaymentCriteria paymentCriteria){
		try{
			return this.pay(paymentCriteria);
		}catch (IllegalArgumentException | ObjectOptimisticLockingFailureException e) {
			paymentRollbackFacade.rollBack(paymentCriteria);
			return null;
		}
	}
	
	@Transactional
	public Payment pay(PaymentCriteria paymentCriteria){
		// 유저 잔액 차감
		userService.useBalance(BalanceCommand.of(paymentCriteria.getUserId(), paymentCriteria.getAmount()));
		// 결제
		Payment payment = paymentService.createPayment(PaymentCommand.of(paymentCriteria.getOrderId()));
		// 주문 완료 처리
		orderService.orderSuccess(OrderAction.of(paymentCriteria.getOrderId()));	
		return payment;
	}
}
