package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long>{
	Optional<UserCoupon> findByUserId(Long userId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT uc FROM UserCoupon uc WHERE uc.id = :id")
	Optional<UserCoupon> findByIdForUpdate(@Param("id") Long userCouponId);
	
	List<UserCoupon> findAllByUserId(Long userId);
	
	@Modifying
	@Query("UPDATE UserCoupon uc SET uc.status = 'EXPIRED' "
			+ "WHERE uc.couponId IN (SELECT c.id FROM Coupon c WHERE c.expireAt < CURRENT_TIMESTAMP ) "
			+ "AND uc.status='AVAILABLE'")
	void expire();
	
	Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);	
}
