package kr.hhplus.be.server.interfaces.payment.dto;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
	private long paymentId;
	
	private PaymentStatus status;
	
	public static PaymentResponse from(Payment payment) {
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.paymentId = payment.getId();
		paymentResponse.status = payment.getStatus();
		return paymentResponse;
	}
}
