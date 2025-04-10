package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long orderId;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	private LocalDateTime paymentAt;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public Payment(long orderId) {
		this.orderId = orderId;
		this.status = PaymentStatus.COMPLETED;
		this.paymentAt = LocalDateTime.now();
		this.createdAt = LocalDateTime.now();
	}
}
