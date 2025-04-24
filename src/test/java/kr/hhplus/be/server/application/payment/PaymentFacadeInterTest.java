package kr.hhplus.be.server.application.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.application.payment.facade.PaymentFacade;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;

@SpringBootTest
@Testcontainers
public class PaymentFacadeInterTest {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	UserCouponRepository userCouponRepository;
	
	@Autowired
	PaymentFacade paymentFacade;
	
	@Autowired
	EntityManager em;
	
	@BeforeEach
	void clearTables() {
	    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE orders").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE user").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE product").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE user_coupon").executeUpdate();
	    em.createNativeQuery("TRUNCATE TABLE payment").executeUpdate();
	    em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}
	
	@Test
	@DisplayName("결제 성공")
	@Transactional
	@Rollback
	void payment(){
		// given
		User user = new User("사용자");
		user.chargeBalance(2000);
		userRepository.save(user);
		
		Order order = new Order(1L, 1000);
		orderRepository.save(order);
		
		// when
		PaymentCriteria paymentCriteria = new PaymentCriteria(1L, 1L, 1000);
		Payment payment = paymentFacade.payment(paymentCriteria);
		
		// then
		assertThat(payment.getOrderId()).isEqualTo(1L);
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		User resultUser = userRepository.findById(1L);
		assertThat(resultUser.getBalance()).isEqualTo(1000);
	}
	
	@Test
	@DisplayName("잔액 부족 결제 실패")
	@Transactional
	@Rollback
	void paymentFail(){
		// given
		User user = new User("사용자");
		user.chargeBalance(1000);
		userRepository.save(user);
		
		Product product = new Product("상품", 2000, 300);
		productRepository.save(product);
		
		Order order = new Order(1L, 2000);
		orderRepository.save(order);
		
		UserCoupon userCoupon = new UserCoupon(user.getId(), 1L);
		userCouponRepository.save(userCoupon);
		OrderDiscount orderDiscount = new OrderDiscount(order.getId(), 1L, 2000);
		order.discount(orderDiscount);
		
		// when
		PaymentCriteria paymentCriteria = new PaymentCriteria(1L, 1L, 2000);
		paymentFacade.payment(paymentCriteria);
		
		// then
		Order resultOrder = orderRepository.findById(1L);
		assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
		
		Product resultProduct = productRepository.findById(product.getId());
		assertThat(resultProduct.getStock()).isEqualTo(300);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(userCoupon.getId());
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
	}
}
