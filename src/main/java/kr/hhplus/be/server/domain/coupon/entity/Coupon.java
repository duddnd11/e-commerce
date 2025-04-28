package kr.hhplus.be.server.domain.coupon.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
	
	private LocalDateTime expireAt;
	
	public Coupon(String name, CouponType type, int value, int quantity) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.quantity = quantity;
		this.status = CouponStatus.ISSUABLE;
		this.createdAt = LocalDateTime.now();
	}
	
	public void issue() {
		if(this.status.equals(CouponStatus.SOLD_OUT)) {
			throw new IllegalArgumentException("쿠폰이 전부 소진되었습니다. 발급이 불가능 합니다.");
		}
		
		this.issuedQuantity++;
		if(this.quantity == this.issuedQuantity) {
			this.status = CouponStatus.SOLD_OUT;
		}
	}
	
	public void setExpireAt(long days) {
		this.expireAt = LocalDateTime.now().plusDays(days);
	}
}
