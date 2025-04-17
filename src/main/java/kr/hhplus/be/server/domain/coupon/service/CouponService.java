package kr.hhplus.be.server.domain.coupon.service;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.DiscountCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
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
	
	public UserCoupon cancel(UserCouponCommand userCouponCommand) {
		if(userCouponCommand.getUserCouponId() != null) {
			UserCoupon userCoupon = userCouponRepository.findById(userCouponCommand.getUserCouponId());
			userCoupon.cancel();
			return userCoupon;
		}else {
			return null;
		}
	}
}
