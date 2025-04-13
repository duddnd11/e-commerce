package kr.hhplus.be.server.interfaces.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.payment.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;

@Tag(name="Payment API", description = "결제 관련")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentFacade paymentFacade;
	
	/**
	 * 결제
	 * @param paymentRequest
	 * @return
	 */
	@Operation(summary = "주문 결제")
	@PostMapping
	public ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest){
		Payment payment = paymentFacade.payment(paymentRequest.toPaymentCriteria());
		return ResponseEntity.ok(PaymentResponse.from(payment));
	}
}
