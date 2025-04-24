package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long>{
}
