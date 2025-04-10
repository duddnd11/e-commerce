package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;
	
	public UserCoupon useCoupon(long userCouponId) {
		UserCoupon userCoupon =  userCouponRepository.findById(userCouponId);
		userCoupon.use();
		return userCouponRepository.save(userCoupon);
	}
	
	public Coupon getCoupon(long couponId) {
		return couponRepository.findById(couponId);
	}
	
	public UserCoupon issue(CouponCommand couponCommand) {
		Coupon coupon = couponRepository.findById(couponCommand.getCouponId());
		coupon.issue();
		UserCoupon userCoupon = new UserCoupon(couponCommand.getUserId(), couponCommand.getCouponId());
		return userCoupon;
	}
	
	public int calDiscountValue(DiscountCommand discountCommand) {
		Coupon coupon = couponRepository.findById(discountCommand.getCouponId());
		if(coupon.getType().equals(CouponType.PRICE)) {
			return coupon.getValue();
		}else {
			return discountCommand.getTotalPrice() * coupon.getValue() / 100; 
		}
	}
}
