package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private PaymentRepository paymentRepository;
	
	public Payment createPayment(PaymentCommand paymentCommand) {
		Payment payment = new Payment(paymentCommand.getOrderId());
		return paymentRepository.save(payment);
	}
}
