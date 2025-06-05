package kr.hhplus.be.server.config;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@Profile("local")
@RequiredArgsConstructor
public class TestDataInit {
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	private final ProductRepository productRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	
	@PostConstruct
    public void insertData() {
        for (int i = 1; i <= 100; i++) {
            User user = new User("user" + i);
            user.chargeBalance(1000000);
            userRepository.save(user);
        }
        
        Coupon coupon = new Coupon("coupon", CouponType.PRICE, 2000, 100000);
        couponRepository.save(coupon);
        String couPonkey = "coupon:stock:" + coupon.getId();
	    for (int i = 0; i < 100000; i++) {
	        redisTemplate.opsForList().rightPush(couPonkey, "coupon");
	    }
        
        for (int i = 1; i <= 50; i++) {
        	Product product = new Product("product" + i, 1000 * i, 100000 * i);
        	productRepository.save(product);
        }
        
        LocalDate startDate = LocalDate.of(2025, 5, 31);
        LocalDate endDate = LocalDate.of(2025, 6, 4);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String rankingKey = "product:ranking:" + date;
            
            // 예시 더미 데이터: productId 1~3번까지, 점수는 랜덤 (혹은 고정)
            redisTemplate.opsForZSet().add(rankingKey, "1", ThreadLocalRandom.current().nextInt(1, 101));
            redisTemplate.opsForZSet().add(rankingKey, "2", ThreadLocalRandom.current().nextInt(1, 101));
            redisTemplate.opsForZSet().add(rankingKey, "3", ThreadLocalRandom.current().nextInt(1, 101));
            redisTemplate.opsForZSet().add(rankingKey, "4", ThreadLocalRandom.current().nextInt(1, 101));
            redisTemplate.opsForZSet().add(rankingKey, "5", ThreadLocalRandom.current().nextInt(1, 101));
        }
    }
}
