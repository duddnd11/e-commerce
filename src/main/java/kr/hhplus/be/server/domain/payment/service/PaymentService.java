package kr.hhplus.be.server.domain.payment.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
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
