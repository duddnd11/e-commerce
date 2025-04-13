package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;

public class PaymentTest {

	@Test
	@DisplayName("주문")
	void payment() {
		// given
		PaymentCommand paymentCommand = PaymentCommand.of(1L);
		
		// when
		Payment payment = new Payment(paymentCommand.getOrderId());
		
		// then
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}
}
