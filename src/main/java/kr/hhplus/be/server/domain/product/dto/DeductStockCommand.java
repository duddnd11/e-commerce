package kr.hhplus.be.server.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeductStockCommand {
	private long productId;
	private int quantity;
	
	public static DeductStockCommand of(long productId, int quantity) {
		return new DeductStockCommand(productId, quantity);
	}
}
