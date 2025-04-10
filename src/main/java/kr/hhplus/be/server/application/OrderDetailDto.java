package kr.hhplus.be.server.application;

import lombok.Getter;

@Getter
public class OrderDetailDto {
	private long productId;
	
	private int quantity;
}
