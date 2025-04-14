package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;

public class CouponTest {

	@Test
	@DisplayName("유저 쿠폰 사용")
	void useCoupon() {
		// given
		UserCoupon userCoupon = new UserCoupon(1L, 1L);
		
		// when
		userCoupon.use();
		
		// then
		assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}
	
	@Test
	@DisplayName("이미 사용한 쿠폰 사용 실패")
	void usedCoupon() {
		// given
		UserCoupon userCoupon = new UserCoupon(1L, 1L);
		userCoupon.use();
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> userCoupon.use());
	}
	
	@Test
	@DisplayName("쿠폰 발급")
	void issueCoupon() {
		// given
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 2);
		
		// when
		coupon.issue();
		
		// then
		assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
		assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ISSUABLE);
		
		// when
		coupon.issue();
		
		// then
		assertThat(coupon.getIssuedQuantity()).isEqualTo(2);
		assertThat(coupon.getStatus()).isEqualTo(CouponStatus.SOLD_OUT);
	}
	
	@Test
	@DisplayName("쿠폰 발급 실패, 소진된 쿠폰")
	void soldOutCoupon() {
		// given
		Coupon coupon = new Coupon("쿠폰1", CouponType.PRICE, 1000, 1);
		coupon.issue();
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> coupon.issue());
	}
}
