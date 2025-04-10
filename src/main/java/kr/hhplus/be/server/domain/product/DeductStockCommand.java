package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeductStockCommand {
	long productId;
	int quantity;
	
	public static DeductStockCommand of(long productId, int quantity) {
		return new DeductStockCommand(productId, quantity);
	}
}
