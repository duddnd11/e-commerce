package kr.hhplus.be.server.domain.order.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	
	private Long userCouponId;
	
	private int totalPrice;
	
	private int finalPrice;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	private LocalDateTime orderAt;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public Order(OrderCommand orderCommand) {
		this.userId = orderCommand.getUserId();
		for (OrderDetailCommand orderDetailCommand : orderCommand.getOrderDetailCommands()) {
			this.totalPrice += orderDetailCommand.getPrice() * orderDetailCommand.getQuantity();
		}
		
		this.status = OrderStatus.PENDING;
		this.createdAt = LocalDateTime.now();
	}
	
	public void discount(OrderDiscount orderDiscount) {
		if(orderDiscount.getDiscountValue() <= 0) {
			throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
		}
		this.userCouponId = orderDiscount.getUserCouponId();
		this.finalPrice = this.totalPrice - orderDiscount.getDiscountValue();
	}
	
	public void success() {
		this.status = OrderStatus.CONFIRMED;
		this.orderAt = LocalDateTime.now();
	}

	public void cancel() {
		this.status = OrderStatus.CANCELED;
	}
}
