package kr.hhplus.be.server.application.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCriteria {
	private long userId;
	private long orderId;
	private int amount;
}
