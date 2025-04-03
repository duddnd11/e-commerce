package kr.hhplus.be.server.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Order API", description = "주문 관련")
@RestController
@RequestMapping("/order")
public class OrderController {
	
	/**
	 * 주문
	 * @param orderRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest orderRequest){
		return ResponseEntity.ok(new OrderResponse(1L, 10000, 8000));
	}
}
