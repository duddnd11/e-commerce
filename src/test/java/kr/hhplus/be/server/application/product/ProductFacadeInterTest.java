package kr.hhplus.be.server.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.product.dto.TopSellingProductInfo;
import kr.hhplus.be.server.application.product.facade.ProductFacade;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Testcontainers
public class ProductFacadeInterTest {
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;
	
	@MockitoSpyBean
	OrderService orderService;
	
	@Autowired
	EntityManager em;
	
	@Autowired
	ProductFacade productFacade;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Test
	@DisplayName("상위 상품 조회")
	void topSellingProducts() {
		redisTemplate.delete("topSellingProduct::topSellingProduct");
	    Set<String> keys = redisTemplate.keys("product:ranking:*");

	    if (keys != null && !keys.isEmpty()) {
	        redisTemplate.delete(keys);
	    }
		
		for(int i=1; i<=10 ;i++) {
			Product product = new Product("상품"+i,i*100,100);
			productRepository.save(product);
			
			Order order = new Order(1L, 1000);
			order.success();
			ReflectionTestUtils.setField(order, "orderAt", LocalDateTime.now().minusDays(1));
			orderRepository.save(order);
			
			OrderDetailCommand orderDetailCommand = new OrderDetailCommand(i, i*10, 100);
			OrderDetail orderDetail = new OrderDetail(order.getId(), orderDetailCommand);
			orderDetailRepository.save(orderDetail);
			
			String key = "product:ranking:"+LocalDate.now().minusDays(1);
			redisTemplate.opsForZSet().add(key, String.valueOf(orderDetail.getProductId()), orderDetail.getQuantity());
		}
		
		LocalDate from = LocalDate.now().minusDays(5);
		LocalDateTime fromDate = from.atStartOfDay();
		
		ValueOperations<String, Object> ops = redisTemplate.opsForValue();
		Object cachedValue = ops.get("topSellingProduct::topSellingProduct");
		
		assertThat(cachedValue).isNull();
		
		long startNoCache = System.nanoTime();
		List<TopSellingProductInfo> topSellingProducts = productFacade.topSellingProduct(fromDate);
		long endNoCache = System.nanoTime();
		long durationNoCache = endNoCache - startNoCache; // 캐시 없이 응답속도 계산
		
		assertThat(topSellingProducts.get(0).getTotalQuantity()).isEqualTo(100);
		assertThat(topSellingProducts.get(1).getTotalQuantity()).isEqualTo(90);
		assertThat(topSellingProducts.get(2).getTotalQuantity()).isEqualTo(80);
		
		Object cachedValue2 = ops.get("topSellingProduct::topSellingProduct");
		
		assertThat(cachedValue2).isNotNull();
		
		// 두번 호출
		long startWithCache = System.nanoTime();
		productFacade.topSellingProduct(fromDate);
		long endWithCache = System.nanoTime();
	    long durationWithCache = endWithCache - startWithCache; // 캐시 사용 응답속도 계산
		
		// 캐시 적용 확인 
		verify(orderService, times(1)).topSellingProduct(fromDate);
		log.info("No cache duration (ms): " + durationNoCache / 1_000_000);
	    log.info("With cache duration (ms): " + durationWithCache / 1_000_000);
		redisTemplate.delete("topSellingProduct::topSellingProduct");
	}
}
