package kr.hhplus.be.server.domain.order.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import lombok.Getter;

@Entity
@Getter
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long orderId;
	
	private Long productId;
	
	private int quantity;
	
	private int totalPrice;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public OrderDetail(long orderId, OrderDetailCommand orderDetailCommand) {
		this.orderId = orderId;
		this.productId = orderDetailCommand.getProductId();
		this.quantity = orderDetailCommand.getQuantity();
		this.totalPrice = orderDetailCommand.getQuantity() * orderDetailCommand.getPrice();
		this.createdAt = LocalDateTime.now();
	}
}
