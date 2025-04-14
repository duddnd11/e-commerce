package kr.hhplus.be.server.order;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.coupon.UserCoupon;
import kr.hhplus.be.server.user.User;
import lombok.Getter;

@Entity
@Getter
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToOne
	@JoinColumn(name = "user_coupon_id")
	private UserCoupon userCoupon;
	
	private int totalPrice;
	
	private int finalPrice;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	private LocalDateTime orderAt;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
}
