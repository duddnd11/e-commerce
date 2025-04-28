package kr.hhplus.be.server.infrastructure.coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long>{
	Optional<UserCoupon> findByUserId(Long userId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select uc from UserCoupon uc where uc.id = :id")
	Optional<UserCoupon> findByIdForUpdate(@Param("id") Long userCouponId);
}
