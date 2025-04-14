package kr.hhplus.be.server.interfaces.payment.dto;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import lombok.Getter;

@Getter
public class PaymentRequest {
	private long userId;
	private long orderId;
	private int amount;
	
	public PaymentCriteria toPaymentCriteria() {
		PaymentCriteria paymentCriteria =  new PaymentCriteria(this.userId, this.orderId, this.amount);
		return paymentCriteria;
	}
}
