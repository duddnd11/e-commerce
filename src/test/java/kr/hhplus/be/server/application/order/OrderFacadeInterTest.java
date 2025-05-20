package kr.hhplus.be.server.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.application.order.dto.OrderDetailCriteria;
import kr.hhplus.be.server.application.order.facade.OrderFacade;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Slf4j
public class OrderFacadeInterTest {
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	@Autowired
	CouponRepository couponRepository;
	
	@Autowired
	UserCouponRepository userCouponRepository;
	
	@Autowired
	OrderFacade orderFacade;
	
	@Autowired
	EntityManager em;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Test
	@DisplayName("주문 성공")
	void order() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(product1.getId(), 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(product2.getId(), 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, 0L, orderDetails);
		
		// when
		Order order = orderFacade.order(orderCriteria);
		
		// then
		// 총 주문 금액 확인
		assertThat(order.getTotalPrice()).isEqualTo(16000);
		
		// 주문 상품 목록 확인
		List<OrderDetail> orderDetail = orderDetailRepository.findByOrderId(order.getId());
		assertThat(orderDetail).hasSize(2);
		assertThat(orderDetail).extracting("productId", "quantity", "totalPrice")
								.containsExactly(
								        tuple(product1.getId(), 10, 10000),
								        tuple(product2.getId(), 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(product1.getId());
		Product resultProduct2 = productRepository.findById(product2.getId());
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
	}
	
	@Test
	@DisplayName("쿠폰 사용 주문 성공")
	void orderCoupon() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PRICE, 2000, 100);
		couponRepository.save(coupon);
		redisTemplate.opsForHash().delete("coupon:"+coupon.getId(), 1L);
		redisTemplate.opsForHash().putIfAbsent("coupon:"+coupon.getId(), 1L, 1);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(product1.getId(), 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(product2.getId(), 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, coupon.getId(), orderDetails);
		
		// when
		Order order = orderFacade.order(orderCriteria);
		
		// then
		// 총 주문 금액 확인
		assertThat(order.getTotalPrice()).isEqualTo(16000);
		assertThat(order.getFinalPrice()).isEqualTo(14000);
		
		// 주문 상품 목록 확인
		List<OrderDetail> orderDetail = orderDetailRepository.findByOrderId(order.getId());
		assertThat(orderDetail).hasSize(2);
		assertThat(orderDetail).extracting("productId", "quantity", "totalPrice")
								.containsExactly(
								        tuple(product1.getId(), 10, 10000),
								        tuple(product2.getId(), 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(product1.getId());
		Product resultProduct2 = productRepository.findById(product2.getId());
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(order.getUserCouponId());
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}
	
	@Test
	@DisplayName("퍼센트 쿠폰 사용 주문 성공")
	void orderPercentCoupon() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PERCENT, 23, 100);
		couponRepository.save(coupon);
		redisTemplate.opsForHash().delete("coupon:"+coupon.getId(), 1L);
		redisTemplate.opsForHash().putIfAbsent("coupon:"+coupon.getId(), 1L, 1);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(product1.getId(), 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(product2.getId(), 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, coupon.getId(), orderDetails);
		
		// when
		Order order = orderFacade.order(orderCriteria);
		
		// then
		// 총 주문 금액 확인
		assertThat(order.getTotalPrice()).isEqualTo(16000);
		assertThat(order.getFinalPrice()).isEqualTo(12320);
		
		// 주문 상품 목록 확인
		List<OrderDetail> orderDetail = orderDetailRepository.findByOrderId(order.getId());
		assertThat(orderDetail).hasSize(2);
		assertThat(orderDetail).extracting("productId", "quantity", "totalPrice")
								.containsExactly(
								        tuple(product1.getId(), 10, 10000),
								        tuple(product2.getId(), 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(product1.getId());
		Product resultProduct2 = productRepository.findById(product2.getId());
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(order.getUserCouponId());
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}
	
	@Test
	@DisplayName("재고 부족 주문 실패")
	void orderFail() {
		// given
		Product product1 = new Product("상품1", 1000, 3);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(product1.getId(), 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(product2.getId(), 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, 1L, orderDetails);
		
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> orderFacade.order(orderCriteria));
		Product resultProduct1 = productRepository.findById(product1.getId());
		assertThat(resultProduct1.getStock()).isEqualTo(3);
		Product resultProduct2 = productRepository.findById(product2.getId());
		assertThat(resultProduct2.getStock()).isEqualTo(70);
	}
	
	@Test
	@DisplayName("사용불가 쿠폰 주문 실패")
	void orderFailCoupon() {
		//given
		Product product = new Product("상품1", 1000, 3);
		productRepository.save(product);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria = new OrderDetailCriteria(product.getId(), 10);
		orderDetails.add(orderDetailCriteria);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		
		OrderCriteria orderCriteria = new OrderCriteria(1L, coupon.getId(), orderDetails);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> orderFacade.order(orderCriteria));
		Product resultProduct = productRepository.findById(product.getId());
		assertThat(resultProduct.getStock()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("주문 동시성 상품 재고 테스트")
	void orderConcurrentStock() throws InterruptedException {
		// given
		Product product = new Product("상품1", 1000, 1000);
		productRepository.save(product);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria = new OrderDetailCriteria(product.getId(), 3);
		orderDetails.add(orderDetailCriteria);
		
		// when
		int numberOfThread = 100;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		
		// when
		for (int i = 0; i < numberOfThread; i++) {
			OrderCriteria orderCriteria = new OrderCriteria(i, 0L, orderDetails);
		    executorService.execute(() -> {
		        try {
		            barrier.await();
		            orderFacade.order(orderCriteria);
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            countDownLatch.countDown();
		        }
		    });
		}
		
		countDownLatch.await();
		executorService.shutdown();
		
		// then
		// 상품 재고 확인
		Product resultProduct = productRepository.findById(product.getId());
		assertThat(resultProduct.getStock()).isEqualTo(700);
	}
	
	@Test
	@DisplayName("주문 동시성 한 사용자가 한 쿠폰을 여러번 사용하는 경우")
	void orderConcurrentUserCopon() throws InterruptedException {
		// given
		Product product1 = new Product("상품1", 1000, 1000);
		Product product2 = new Product("상품2", 2000, 2000);
		productRepository.save(product1);
		productRepository.save(product2);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(product1.getId(), 3);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(product2.getId(), 5);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PRICE, 1000, 100);
		couponRepository.save(coupon);
		redisTemplate.opsForHash().delete("coupon:"+coupon.getId(), 1L);
		redisTemplate.opsForHash().put("coupon:"+coupon.getId(), 1L, 1);
		
		// when
		int numberOfThread = 3;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		
		AtomicInteger failCount = new AtomicInteger();
		
		// when
		for (int i = 0; i < numberOfThread; i++) {
			OrderCriteria orderCriteria = new OrderCriteria(1L, coupon.getId(), orderDetails);
		    executorService.execute(() -> {
		        try {
		            barrier.await();
		            orderFacade.order(orderCriteria);
		        } catch (IllegalArgumentException e) {
		        	failCount.incrementAndGet();
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            countDownLatch.countDown();
		        }
		    });
		}
		
		countDownLatch.await();
		executorService.shutdown();
		
		// then
		assertThat(failCount.get()).isEqualTo(2);
		Product resultProduct1 = productRepository.findById(product1.getId());
		assertThat(resultProduct1.getStock()).isEqualTo(997);
		Product resultProduct2 = productRepository.findById(product2.getId());
		assertThat(resultProduct2.getStock()).isEqualTo(1995);
	}
}
