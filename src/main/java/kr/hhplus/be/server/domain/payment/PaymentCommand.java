package kr.hhplus.be.server.domain.payment;

import lombok.Getter;

@Getter
public class PaymentCommand {
	private long orderId;
	
	public static PaymentCommand of(long orderId) {
		PaymentCommand paymentCommand = new PaymentCommand();
		paymentCommand.orderId = orderId;
		return paymentCommand;
	}
}
