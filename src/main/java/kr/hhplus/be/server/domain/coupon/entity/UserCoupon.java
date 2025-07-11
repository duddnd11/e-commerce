package kr.hhplus.be.server.domain.coupon.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
    name = "user_coupon",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
    }
)
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
	
	public void cancel() {
		this.status = UserCouponStatus.AVAILABLE;
		this.updatedAt = LocalDateTime.now();
	}
	
	public void expiration() {
		this.status = UserCouponStatus.EXPIRED;
		this.updatedAt = LocalDateTime.now();
	}
}
