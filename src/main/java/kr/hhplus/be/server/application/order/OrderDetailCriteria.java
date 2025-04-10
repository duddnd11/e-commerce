package kr.hhplus.be.server.application.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDetailCriteria {
	private long productId;
	
	private int quantity;
}
