package kr.hhplus.be.server.application.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.product.dto.TopSellingProductInfo;
import kr.hhplus.be.server.application.product.facade.ProductFacade;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;

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
	
	@Autowired
	EntityManager em;
	
	@Autowired
	ProductFacade productFacade;
	
	@Test
	@DisplayName("상위 상품 조회")
	@Transactional
	void topSellingProducts() {
		for(int i=1; i<=10 ;i++) {
			Product product = new Product("상품"+i,i*100,100);
			productRepository.save(product);
			
			Order order = new Order(1L, 1000);
			order.success();
			orderRepository.save(order);
			
			OrderDetailCommand orderDetailCommand = new OrderDetailCommand(i, i*10, 100);
			OrderDetail orderDetail1 = new OrderDetail(order.getId(), orderDetailCommand);
			orderDetailRepository.save(orderDetail1);
		}
		
		em.flush();
		em.clear();
		
		List<TopSellingProductInfo> topSellingProducts = productFacade.topSellingProduct(LocalDateTime.now().minusDays(3));
		assertThat(topSellingProducts.get(0).getTotalQuantity()).isEqualTo(100);
		assertThat(topSellingProducts.get(1).getTotalQuantity()).isEqualTo(90);
		assertThat(topSellingProducts.get(2).getTotalQuantity()).isEqualTo(80);
	}
}
