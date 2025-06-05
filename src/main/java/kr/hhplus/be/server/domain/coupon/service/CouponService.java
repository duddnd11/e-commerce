package kr.hhplus.be.server.domain.coupon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.DiscountCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.repository.CouponRedisRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponRedisRepository couponRedisRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final CouponEvent couponEvent;
	
	public UserCoupon useCoupon(CouponCommand couponCommand) {
		UserCouponResult userCouponResult = couponRedisRepository.use(couponCommand);
		if(userCouponResult != null) {
			UserCoupon userCoupon = new UserCoupon(userCouponResult.getUserId(), userCouponResult.getCouponId());
			userCoupon.use();
			return userCouponRepository.save(userCoupon);
		}else {
			throw new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다.");
		}
	}
	
	public Coupon getCoupon(long couponId) {
		return couponRepository.findById(couponId);
	}
	
	@Transactional
	public UserCouponResult issue(CouponCommand couponCommand) {
		UserCouponResult userCouponResult = couponRedisRepository.issue(couponCommand);
		if(userCouponResult != null) {
			eventPublisher.publishEvent(couponCommand);
		}
		return userCouponResult;
	}
	
	public void issuEvent(CouponCommand couponCommand) {
		couponEvent.couponIssue(couponCommand);
	}
	
	public void saveUserCoupon(CouponCommand couponCommand) {
		System.out.println("saveUserCoupon");
		try {
			Optional<UserCoupon> exists = userCouponRepository.findByUserIdAndCouponId(couponCommand.getUserId(), couponCommand.getCouponId());
			if (exists.isEmpty()) {
			    UserCoupon userCoupon = new UserCoupon(couponCommand.getUserId(), couponCommand.getCouponId());
			    userCouponRepository.save(userCoupon);
			}
		} catch (DataIntegrityViolationException e) {
		    log.info("중복 쿠폰 저장 시도: userId={}, couponId={}", couponCommand.getUserId(), couponCommand.getCouponId());
		}
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
	
	public List<UserCouponResult> getUserCoupons(long userId){
		List<UserCouponResult> redisUserCoupons = couponRedisRepository.findUserCoupon(userId);
		List<UserCoupon> userCoupons = userCouponRepository.findAllByUserId(userId);
	    List<UserCouponResult> userCouponsResult = userCoupons.stream()
	            .map(uc -> UserCouponResult.of(uc.getUserId(), uc.getCouponId(), uc.getStatus()))
	            .collect(Collectors.toList());
	    
	    List<UserCouponResult> allResults = new ArrayList<>();
	    allResults.addAll(userCouponsResult);
	    allResults.addAll(redisUserCoupons);
	    
		return allResults;
	}
	
	public void expire() {
		List<Coupon> expires =couponRepository.findExpire();
		
		for(Coupon c : expires) {
			couponRedisRepository.expire(c.getId());
		}
	}
}
