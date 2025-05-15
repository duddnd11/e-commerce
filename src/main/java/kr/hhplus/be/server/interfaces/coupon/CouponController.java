package kr.hhplus.be.server.interfaces.coupon;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.UserCouponResponse;
import lombok.RequiredArgsConstructor;

@Tag(name="쿠폰 API", description = "쿠폰 관련 (쿠폰 목록, 발급)")
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
	
	private final CouponService couponService;
	
	/**
	 * 유저 보유 쿠폰 목록
	 * @param userId
	 * @return
	 */
	@Operation(summary = "유저 보유 쿠폰 목록")
	@GetMapping("/list/{userId}")
	public ResponseEntity<List<UserCouponResponse>> getCouponList(@PathVariable("userId") long userId){
		List<UserCouponResult> userCoupons = couponService.getUserCoupons(userId);
		List<UserCouponResponse> userCouponResponse = userCoupons.stream()
				.map(uc -> UserCouponResponse.from(uc.getUserId(), uc.getCouponId(), uc.getStatus()))
				.collect(Collectors.toList());
		return ResponseEntity.ok(userCouponResponse);
	}
	
	/**
	 * 선착순 쿠폰 발급
	 * @param couponRequest
	 * @return
	 */
	@Operation(summary = "선착순 쿠폰 발급")
	@PostMapping("/issue")
	public ResponseEntity<UserCouponResponse> issueCoupon(@RequestBody CouponRequest couponRequest){
		UserCouponResult userCoupon = couponService.issue(couponRequest.toCouponCommand());
		return ResponseEntity.ok(UserCouponResponse.from(userCoupon.getUserId(), userCoupon.getCouponId(), UserCouponStatus.AVAILABLE));
	}
}
