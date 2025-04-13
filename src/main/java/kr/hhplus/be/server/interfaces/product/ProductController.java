package kr.hhplus.be.server.interfaces.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;

@Tag(name="Product API", description = "상품 관리 (조회, 상위 상품 조회)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;

	/**
	 * 상품 조회
	 * @param productId
	 * @return
	 */
	@Operation(summary = "상품 조회")
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponse> getProduct(@PathVariable("productId")long productId){
		Product product = productService.getProduct(ProductCommand.of(productId));
		return ResponseEntity.ok(ProductResponse.from(product));
	}
	
	/**
	 * 최근 상위 5개 상품 조회
	 * @return
	 */
	@Operation(summary = "최근 상위 5개 상품 조회")
	@GetMapping("/top")
	public ResponseEntity<List<ProductResponse>> getTopProductList(){
		List<ProductResponse> topProductList = new ArrayList<ProductResponse>();
		topProductList.add(new ProductResponse(1L, "키보드", 50000, 100));
		topProductList.add(new ProductResponse(2L, "마우스", 20000, 200));
		topProductList.add(new ProductResponse(3L, "모니터", 70000, 150));
		topProductList.add(new ProductResponse(4L, "이어폰", 15000, 130));
		topProductList.add(new ProductResponse(5L, "헤드셋", 200000, 73));
		
		return ResponseEntity.ok(topProductList);
	}
}
