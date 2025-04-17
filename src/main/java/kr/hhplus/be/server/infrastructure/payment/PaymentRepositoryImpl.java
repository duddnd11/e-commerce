package kr.hhplus.be.server.infrastructure.payment;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository{
	private final PaymentJpaRepository jpaRepository;
	
	@Override
	public Payment save(Payment payment) {
		return jpaRepository.save(payment);
	}

}
