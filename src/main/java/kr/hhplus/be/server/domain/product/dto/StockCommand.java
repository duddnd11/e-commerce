package kr.hhplus.be.server.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockCommand {
	private long productId;
	private int quantity;
	
	public static StockCommand of(long productId, int quantity) {
		return new StockCommand(productId, quantity);
	}
}
