package kr.hhplus.be.server.coupon;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="쿠폰 API", description = "쿠폰 관련 (쿠폰 목록, 발급)")
@RestController
@RequestMapping("/coupon")
public class CouponController {
	
	/**
	 * 유저 보유 쿠폰 목록
	 * @param userId
	 * @return
	 */
	@Operation(summary = "유저 보유 쿠폰 목록")
	@GetMapping("/list/{userId}")
	public ResponseEntity<List<UserCouponResponse>> getCouponList(@PathVariable("userId") long userId){
		List<UserCouponResponse> userCouponList = new ArrayList<UserCouponResponse>();
		
		userCouponList.add(new UserCouponResponse(1L, "쿠폰이름1", CouponType.PRICE, 1000, UserCouponStatus.AVAILABLE));
		userCouponList.add(new UserCouponResponse(2L, "쿠폰이름2", CouponType.PERCENT, 10, UserCouponStatus.USED));
		userCouponList.add(new UserCouponResponse(3L, "쿠폰이름3", CouponType.PRICE, 1000, UserCouponStatus.USED));
		userCouponList.add(new UserCouponResponse(4L, "쿠폰이름4", CouponType.PERCENT, 20, UserCouponStatus.AVAILABLE));
		
		return ResponseEntity.ok(userCouponList);
	}
	
	/**
	 * 선착순 쿠폰 발급
	 * @param couponRequest
	 * @return
	 */
	@Operation(summary = "선착순 쿠폰 발급")
	@PostMapping("/issue")
	public ResponseEntity<CouponResponse> issueCoupon(@RequestBody CouponRequest couponRequest){
		return ResponseEntity.ok(new CouponResponse(1L, "쿠폰1", CouponType.PRICE, 2000));
	}
}
