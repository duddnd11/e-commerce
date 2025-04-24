package kr.hhplus.be.server.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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

@SpringBootTest
@Testcontainers
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

	@BeforeEach
	void clearTables() {
	    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE product").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE order_detail").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE coupon").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE user_coupon").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE orders").executeUpdate();
	    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}
	
	@Test
	@DisplayName("주문 성공")
	@Transactional
	@Rollback
	void order() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(1L, 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(2L, 3);
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
								        tuple(1L, 10, 10000),
								        tuple(2L, 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(1L);
		Product resultProduct2 = productRepository.findById(2L);
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
	}
	
	@Test
	@DisplayName("쿠폰 사용 주문 성공")
	@Transactional
	@Rollback
	void orderCoupon() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PRICE, 2000, 100);
		couponRepository.save(coupon);
		UserCoupon userCoupon = new UserCoupon(1L, 1L);
		userCouponRepository.save(userCoupon);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(1L, 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(2L, 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, 1L, orderDetails);
		
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
								        tuple(1L, 10, 10000),
								        tuple(2L, 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(1L);
		Product resultProduct2 = productRepository.findById(2L);
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(1L);
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}
	
	@Test
	@DisplayName("퍼센트 쿠폰 사용 주문 성공")
	@Transactional
	@Rollback
	void orderPercentCoupon() {
		// given
		Product product1 = new Product("상품1", 1000, 100);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		Coupon coupon = new Coupon("쿠폰", CouponType.PERCENT, 23, 100);
		couponRepository.save(coupon);
		UserCoupon userCoupon = new UserCoupon(1L, 1L);
		userCouponRepository.save(userCoupon);
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(1L, 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(2L, 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, 1L, orderDetails);
		
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
								        tuple(1L, 10, 10000),
								        tuple(2L, 3, 6000)
								        );
		
		// 상품 재고 확인
		Product resultProduct1 = productRepository.findById(1L);
		Product resultProduct2 = productRepository.findById(2L);
		assertThat(resultProduct1.getStock()).isEqualTo(90);
		assertThat(resultProduct2.getStock()).isEqualTo(67);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(1L);
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}
	
	@Test
	@DisplayName("재고 부족 주문 실패")
	@Transactional
	@Rollback
	void orderFail() {
		// given
		Product product1 = new Product("상품1", 1000, 3);
		Product product2 = new Product("상품2", 2000, 70);
		productRepository.save(product1);
		productRepository.save(product2);
		
		
		List<OrderDetailCriteria> orderDetails = new ArrayList<OrderDetailCriteria>();
		OrderDetailCriteria orderDetailCriteria1 = new OrderDetailCriteria(1L, 10);
		OrderDetailCriteria orderDetailCriteria2 = new OrderDetailCriteria(2L, 3);
		orderDetails.add(orderDetailCriteria1);
		orderDetails.add(orderDetailCriteria2);
		OrderCriteria orderCriteria = new OrderCriteria(1L, 1L, orderDetails);
		
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> orderFacade.order(orderCriteria));
	}
}
