package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
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
