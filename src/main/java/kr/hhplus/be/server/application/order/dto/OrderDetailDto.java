package kr.hhplus.be.server.application.order.dto;

import lombok.Getter;

@Getter
public class OrderDetailDto {
	private long productId;
	
	private int quantity;
}
