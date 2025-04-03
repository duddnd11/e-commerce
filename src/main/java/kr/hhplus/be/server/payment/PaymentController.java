package kr.hhplus.be.server.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Payment API", description = "결제 관련")
@RestController
@RequestMapping("/payment")
public class PaymentController {

	/**
	 * 결제
	 * @param paymentRequest
	 * @return
	 */
	@Operation(summary = "주문 결제")
	@PostMapping
	public ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest){
		return ResponseEntity.ok(new PaymentResponse(1L, PaymentStatus.COMPLETED));
	}
}
