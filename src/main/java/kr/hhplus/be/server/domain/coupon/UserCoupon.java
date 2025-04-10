package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserCoupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	
	private Long couponId;
	
	@Enumerated(EnumType.STRING)
	private UserCouponStatus status;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public UserCoupon(long userId, long couponId) {
		this.userId = userId;
		this.couponId = couponId;
		this.status = UserCouponStatus.AVAILABLE;
		this.createdAt = LocalDateTime.now();
	}
	
	public void use() {
		if(this.status.equals(UserCouponStatus.AVAILABLE)) {
			this.status = UserCouponStatus.USED;
			this.updatedAt = LocalDateTime.now();
		}else {
			throw new IllegalArgumentException("사용할 수 없는 쿠폰입니다.");
		}
	}
}
