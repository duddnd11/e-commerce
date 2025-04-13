package kr.hhplus.be.server.interfaces.product.dto;

import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	private long productId;
	private String name;
	private int price;
	private int stock;
	
	public static ProductResponse from(Product product) {
		ProductResponse productResponse = new ProductResponse();
		productResponse.productId = product.getId();
		productResponse.name = product.getName();
		productResponse.price = product.getPrice();
		productResponse.stock = product.getStock();
		return productResponse;
	}
}
