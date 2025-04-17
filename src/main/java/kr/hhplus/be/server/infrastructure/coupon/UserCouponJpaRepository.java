package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long>{
	UserCoupon findByUserId(Long userId);
}
