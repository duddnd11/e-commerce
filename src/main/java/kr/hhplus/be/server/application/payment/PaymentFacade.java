package kr.hhplus.be.server.application.payment;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.order.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.BalanceCommand;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
	private final PaymentService paymentService;
	private final UserService userService;
	private final OrderService orderService;
	
	public Payment payment(PaymentCriteria paymentCriteria) {
		// 유저 잔액 차감
		try{
			userService.useBalance(BalanceCommand.of(paymentCriteria.getUserId(), paymentCriteria.getAmount()));
		}catch (IllegalArgumentException e) {
			orderService.cancel(OrderResult.of(paymentCriteria.getOrderId()));
		}
		
		// 결제
		Payment payment = paymentService.createPayment(PaymentCommand.of(paymentCriteria.getOrderId()));
		
		// 주문 완료 처리
		orderService.orderSuccess(OrderResult.of(paymentCriteria.getOrderId()));
		return payment;
	}
}
