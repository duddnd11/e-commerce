package kr.hhplus.be.server.interfaces.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.dto.TopSellingProductInfo;
import kr.hhplus.be.server.application.product.facade.ProductFacade;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import kr.hhplus.be.server.interfaces.product.dto.TopSellingProductResponse;
import lombok.RequiredArgsConstructor;

@Tag(name="Product API", description = "상품 관리 (조회, 상위 상품 조회)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	private final ProductFacade productFacade;

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
	public ResponseEntity<List<TopSellingProductResponse>> getTopProductList(){
		List<TopSellingProductInfo> topProducts = productFacade.topSellingProduct(LocalDateTime.now().minusDays(5));
		List<TopSellingProductResponse> topProductsResponse = topProducts.stream()
				.map(tp -> TopSellingProductResponse.from(tp))
				.collect(Collectors.toList());
		
		return ResponseEntity.ok(topProductsResponse);
	}
}
