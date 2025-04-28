package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDiscount;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;

public class OrderTest {
	
	@Test
	@DisplayName("주문 생성")
	void order() {
		// given
		long userId = 1L;
		int totalPrice = 21000;
		
		// when
		Order order = new Order(userId, totalPrice);
		
		// then
		assertThat(order.getTotalPrice()).isEqualTo(21000);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
	}
	
	@Test
	@DisplayName("주문 디테일 생성")
	void orderDetail() {
		// given
		OrderDetailCommand orderDetailCommand1 = new OrderDetailCommand(1L, 3, 1000);
		OrderDetailCommand orderDetailCommand2 = new OrderDetailCommand(2L, 2, 1500);
		OrderDetailCommand orderDetailCommand3 = new OrderDetailCommand(3L, 5, 3000);
		
		// when
		OrderDetail orderDetail1 = new OrderDetail(1L, orderDetailCommand1);
		OrderDetail orderDetail2 = new OrderDetail(2L, orderDetailCommand2);
		OrderDetail orderDetail3 = new OrderDetail(3L, orderDetailCommand3);
		
		// then
		assertThat(orderDetail1.getTotalPrice()).isEqualTo(3000);
		assertThat(orderDetail2.getTotalPrice()).isEqualTo(3000);
		assertThat(orderDetail3.getTotalPrice()).isEqualTo(15000);
	}
	
	@Test
	@DisplayName("주문 성공")
	void success() {
		// given
		Order order = new Order();
		
		// when
		order.success();
		
		// then
		assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
	}
	
	@Test
	@DisplayName("주문 할인 적용")
	void discount() {
		// given
		Order order = new Order(1L, 21000);
		
		OrderDiscount orderDiscount = new OrderDiscount(1L, 1L, 3000);
		
		// when
		order.discount(orderDiscount);
		
		// then
		assertThat(order.getFinalPrice()).isEqualTo(18000);
	}
	
	@Test
	@DisplayName("할인 금액 0이하 실패")
	void discountUnderZero() {
		// given
		Order order = new Order(1L, 21000);
		
		OrderDiscount orderDiscount = new OrderDiscount(1L, 1L, 0);
		
		// then
		assertThrows(IllegalArgumentException.class, () -> order.discount(orderDiscount));
	}
	
	@Test
	@DisplayName("주문 성공")
	void cancel() {
		// given
		Order order = new Order();
		
		// when
		order.cancel();
		
		// then
		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
	}
}
