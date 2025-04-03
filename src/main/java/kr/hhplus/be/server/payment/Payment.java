package kr.hhplus.be.server.payment;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.order.Order;
import lombok.Getter;

@Entity
@Getter
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	private LocalDateTime paymentAt;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
