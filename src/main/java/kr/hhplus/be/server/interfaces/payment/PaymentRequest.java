package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.application.PaymentCriteria;
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
