package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Coupon c where c.id = :id")
	Optional<Coupon> findByIdForUpdate(@Param("id") Long couponId);
	
	@Query("SELECT c FROM Coupon c WHERE c.expireAt < CURRENT_TIMESTAMP")
	List<Coupon> findExpire();
}
