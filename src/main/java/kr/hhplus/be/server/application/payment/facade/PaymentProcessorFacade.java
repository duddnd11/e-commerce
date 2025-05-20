package kr.hhplus.be.server.application.payment.facade;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.domain.order.dto.OrderAction;
import kr.hhplus.be.server.domain.order.dto.OrderEvent;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.user.dto.BalanceCommand;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PaymentProcessorFacade {
	private final PaymentService paymentService;
	private final UserService userService;
	private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;
	
	@Transactional
	public Payment pay(PaymentCriteria paymentCriteria){
		// 유저 잔액 차감
		userService.useBalance(BalanceCommand.of(paymentCriteria.getUserId(), paymentCriteria.getAmount()));
		// 결제
		Payment payment = paymentService.createPayment(PaymentCommand.of(paymentCriteria.getOrderId()));
		// 주문 완료 처리
		orderService.orderSuccess(OrderAction.of(paymentCriteria.getOrderId()));
		
		// 주문 완료 event 발행 -> redis 랭킹 반영
		eventPublisher.publishEvent(new OrderEvent(paymentCriteria.getOrderId()));
		
		return payment;
	}
	
}
