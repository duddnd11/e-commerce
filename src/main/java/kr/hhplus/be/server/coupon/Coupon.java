package kr.hhplus.be.server.coupon;

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
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private CouponType type;
	
	private int value;
	
	private int quantity;
	
	private int issuedQuantity;
	
	@Enumerated(EnumType.STRING)
	private CouponStatus status;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
