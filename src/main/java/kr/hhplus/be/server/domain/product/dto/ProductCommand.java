package kr.hhplus.be.server.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCommand {
	long productId;
	
	public static ProductCommand of(long productId) {
		return new ProductCommand(productId);
	}
}
