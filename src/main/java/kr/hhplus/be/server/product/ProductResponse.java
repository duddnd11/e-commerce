package kr.hhplus.be.server.product;

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
}
