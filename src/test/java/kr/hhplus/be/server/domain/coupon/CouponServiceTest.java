package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;

public class CouponServiceTest {
	
	@Mock
	UserCouponRepository userCouponRepository;

	@InjectMocks
	CouponService couponService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito 초기화
	}
	
	@Test
	@DisplayName("유저 보유 쿠폰 목록 조회")
	void userCoupons() {
		// given
		UserCoupon userCoupon1 = new UserCoupon(1L,1L);
		UserCoupon userCoupon2 = new UserCoupon(1L,2L);
		UserCoupon userCoupon3 = new UserCoupon(1L,3L);
		List<UserCoupon> userCoupons = new ArrayList<UserCoupon>();
		userCoupons.add(userCoupon1);
		userCoupons.add(userCoupon2);
		userCoupons.add(userCoupon3);
		
		when(userCouponRepository.findAllByUserId(1L)).thenReturn(userCoupons);
		
		// when
		List<UserCoupon> resultUserCoupons = couponService.getUserCoupons(1L);
		
		// then
		assertThat(resultUserCoupons.get(0).getCouponId()).isEqualTo(1L);
		assertThat(resultUserCoupons.get(1).getCouponId()).isEqualTo(2L);
		assertThat(resultUserCoupons.get(2).getCouponId()).isEqualTo(3L);
	}
}
