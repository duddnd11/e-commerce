package kr.hhplus.be.server.payment;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
	private long paymentId;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
}
