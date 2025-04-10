package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.order.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.OrderDiscount;
import kr.hhplus.be.server.domain.order.OrderStatus;

public class OrderTest {
	
	@Test
	@DisplayName("주문 생성")
	void order() {
		// given
		List<OrderDetailCommand> orderDetailCommands = new ArrayList<OrderDetailCommand>();
		OrderDetailCommand orderDetailCommand1 = new OrderDetailCommand(1L, 3, 1000);
		OrderDetailCommand orderDetailCommand2 = new OrderDetailCommand(2L, 2, 1500);
		OrderDetailCommand orderDetailCommand3 = new OrderDetailCommand(3L, 5, 3000);
		orderDetailCommands.add(orderDetailCommand1);
		orderDetailCommands.add(orderDetailCommand2);
		orderDetailCommands.add(orderDetailCommand3);
		OrderCommand orderCommand  = new OrderCommand(1L,orderDetailCommands);
		
		// when
		Order order = new Order(orderCommand);
		
		// then
		assertThat(order.getTotalPrice()).isEqualTo(21000);
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
		List<OrderDetailCommand> orderDetailCommands = new ArrayList<OrderDetailCommand>();
		OrderDetailCommand orderDetailCommand1 = new OrderDetailCommand(1L, 3, 1000);
		OrderDetailCommand orderDetailCommand2 = new OrderDetailCommand(2L, 2, 1500);
		OrderDetailCommand orderDetailCommand3 = new OrderDetailCommand(3L, 5, 3000);
		orderDetailCommands.add(orderDetailCommand1);
		orderDetailCommands.add(orderDetailCommand2);
		orderDetailCommands.add(orderDetailCommand3);
		OrderCommand orderCommand  = new OrderCommand(1L,orderDetailCommands);
		Order order = new Order(orderCommand);
		
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
		List<OrderDetailCommand> orderDetailCommands = new ArrayList<OrderDetailCommand>();
		OrderDetailCommand orderDetailCommand1 = new OrderDetailCommand(1L, 3, 1000);
		OrderDetailCommand orderDetailCommand2 = new OrderDetailCommand(2L, 2, 1500);
		OrderDetailCommand orderDetailCommand3 = new OrderDetailCommand(3L, 5, 3000);
		orderDetailCommands.add(orderDetailCommand1);
		orderDetailCommands.add(orderDetailCommand2);
		orderDetailCommands.add(orderDetailCommand3);
		OrderCommand orderCommand  = new OrderCommand(1L,orderDetailCommands);
		Order order = new Order(orderCommand);
		
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
