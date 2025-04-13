package kr.hhplus.be.server.interfaces.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.facade.OrderFacade;
import kr.hhplus.be.server.interfaces.order.dto.OrderRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;

@Tag(name="Order API", description = "주문 관련")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderFacade orderFacade;
	
	/**
	 * 주문
	 * @param orderRequest
	 * @return
	 */
	@Operation(summary = "상품 주문")
	@PostMapping
	public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest orderRequest){
		return ResponseEntity.ok(OrderResponse.from(orderFacade.order(orderRequest.toOrderCriteria())));
	}
}
