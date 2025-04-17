package kr.hhplus.be.server.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResult {
	private Long productId;
	private int quantity;
	private int price;
	
	public static ProductResult of(Long productId, int quantity, int price) {
		ProductResult productResult = new ProductResult(productId, quantity, price);
		return productResult;
	}
}
