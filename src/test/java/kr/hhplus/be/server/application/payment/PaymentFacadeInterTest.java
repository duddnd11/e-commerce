package kr.hhplus.be.server.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.application.payment.facade.PaymentFacade;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.order.DataPlatform;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.enums.PaymentStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Testcontainers
//@Import(TestcontainersConfiguration.class)
//@EmbeddedKafka(partitions = 1, topics = "send-payment")
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
	OrderDetailRepository orderDetailRepository;
	
	@Autowired
	PaymentFacade paymentFacade;
	
	@Autowired
	EntityManager em;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@MockitoBean
	DataPlatform dataPlatform;
	
    private static final AtomicReference<String> receivedMessage = new AtomicReference<>();
	
	@KafkaListener(topics = "send-payment", groupId = "send-payment")
    public void listen(ConsumerRecord<String, String> record) {
        receivedMessage.set(record.value());
        System.out.println("record:"+record.value());
    }

	@Test
	@DisplayName("결제 성공")
	void payment(){
		String key = "product:ranking:"+LocalDate.now();
		redisTemplate.delete(key);
		redisTemplate.opsForZSet().removeRange(key, 0, -1);
		
		// given
		User user = new User("사용자");
		user.chargeBalance(2000);
		userRepository.save(user);
		
		Order order = new Order(user.getId(), 1000);
		orderRepository.save(order);
		
		OrderDetailCommand orderDetailCommand = new OrderDetailCommand(1L, 3, 3000);
		OrderDetail orderDetail = new OrderDetail(order.getId(), orderDetailCommand);
		orderDetailRepository.save(orderDetail);
		
		OrderDetailCommand orderDetailCommand2 = new OrderDetailCommand(2L, 5, 15000);
		OrderDetail orderDetail2 = new OrderDetail(order.getId(), orderDetailCommand2);
		orderDetailRepository.save(orderDetail2);
		
		// when
		PaymentCriteria paymentCriteria = new PaymentCriteria(user.getId(), order.getId(), 1000);
		Payment payment = paymentFacade.payment(paymentCriteria);
		
		// then
		assertThat(payment.getOrderId()).isEqualTo(order.getId());
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		User resultUser = userRepository.findById(user.getId());
		assertThat(resultUser.getBalance()).isEqualTo(1000);
		
		
		// 스코어 확인
		Set<TypedTuple<Object>> ranking = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 1);
		Map<Object, Double> result = ranking.stream()
		        .collect(Collectors.toMap(TypedTuple::getValue, TypedTuple::getScore));
		assertThat(result)
		        .containsEntry("1", 3.0)
		        .containsEntry("2", 5.0);
		
		redisTemplate.delete(key);
		redisTemplate.opsForZSet().removeRange(key, 0, -1);

		verify(dataPlatform, times(1)).paymentDataPlatformSend(order.getId());
	}
	
	@Test
	@DisplayName("잔액 부족 결제 실패")
	void paymentFail(){
		// given
		User user = new User("사용자");
		user.chargeBalance(1000);
		userRepository.save(user);
		
		Product product = new Product("상품", 2000, 300);
		productRepository.save(product);
		
		Order order = new Order(user.getId(), 2000);
		orderRepository.save(order);
		
		OrderDetail orderDetai = new OrderDetail(order.getId(), OrderDetailCommand.of(product.getId(), 10, 2000));
		orderDetailRepository.save(orderDetai);
		
		UserCoupon userCoupon = new UserCoupon(user.getId(), 1L);
		userCouponRepository.save(userCoupon);
		OrderDiscount orderDiscount = new OrderDiscount(order.getId(), userCoupon.getId(), 2000);
		order.discount(orderDiscount);
		
		// when
		PaymentCriteria paymentCriteria = new PaymentCriteria(user.getId(), order.getId(), 2000);
		Payment payment = paymentFacade.payment(paymentCriteria);
		
		// then
		assertThat(payment).isEqualTo(null);
		
		Order resultOrder = orderRepository.findById(order.getId());
		assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
		
		Product resultProduct = productRepository.findById(product.getId());
		assertThat(resultProduct.getStock()).isEqualTo(310);
		
		UserCoupon resultUserCoupon = userCouponRepository.findById(userCoupon.getId());
		assertThat(resultUserCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
	}
	
	@Test
	@DisplayName("결제 동시성 동일 유저 테스트")
	void paymentConcurrent() throws InterruptedException{
		// given
		User user = new User("사용자");
		user.chargeBalance(2000);
		userRepository.save(user);
		
		Product product1 = new Product("상품1", 1000, 100);
		productRepository.save(product1);
		Product product2 = new Product("상품2", 2000, 200);
		productRepository.save(product2);
		Product product3 = new Product("상품3", 3000, 300);
		productRepository.save(product3);
		
		Order order1 = new Order(user.getId(), 10000);
		Order order2 = new Order(user.getId(), 10000);
		Order order3 = new Order(user.getId(), 9000);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		
		OrderDetail orderDetai1 = new OrderDetail(order1.getId(), OrderDetailCommand.of(product1.getId(), 10, 1000));
		orderDetailRepository.save(orderDetai1);
		OrderDetail orderDetai2 = new OrderDetail(order2.getId(), OrderDetailCommand.of(product2.getId(), 5, 2000));
		orderDetailRepository.save(orderDetai2);
		OrderDetail orderDetai3 = new OrderDetail(order3.getId(), OrderDetailCommand.of(product3.getId(), 3, 3000));
		orderDetailRepository.save(orderDetai3);
		
		List<Order> orders = new ArrayList<Order>();
		orders.add(order1);
		orders.add(order2);
		orders.add(order3);
		
		// when
		int numberOfThread = 3;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		AtomicInteger failCount = new AtomicInteger();
		for (int i = 0; i < numberOfThread; i++) {
			PaymentCriteria paymentCriteria = new PaymentCriteria(user.getId(), orders.get(i).getId(), 1000);
		    executorService.execute(() -> {
		        try {
		            barrier.await();
		            Payment payment = paymentFacade.payment(paymentCriteria);
		            if(payment == null) failCount.incrementAndGet();
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
	}
		
}
