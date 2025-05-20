package kr.hhplus.be.server.application.payment.facade;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacade {
	private final PaymentRollbackFacade paymentRollbackFacade;
	private final PaymentProcessorFacade paymentProcessorFacade;
    
	public Payment payment(PaymentCriteria paymentCriteria){
		try{
			return paymentProcessorFacade.pay(paymentCriteria);
		}catch (IllegalArgumentException | ObjectOptimisticLockingFailureException e) {
			paymentRollbackFacade.rollBack(paymentCriteria);
			return null;
		}
	}
}
