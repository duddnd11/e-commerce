package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderDetailCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;

public class OrderServiceTest {

	@Mock
	OrderRepository orderRepository;
	
	@Mock
	OrderDetailRepository orderDetailRepository;
	
	@InjectMocks
	OrderService orderService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito 초기화
	}
	
	@Test
	@DisplayName("주문 요청")
	void order() {
		// given
		List<OrderDetailCommand> orderDetailCommands = new ArrayList<OrderDetailCommand>();
		OrderDetailCommand orderDetail1 = new OrderDetailCommand(1L, 3, 1000);
		OrderDetailCommand orderDetail2 = new OrderDetailCommand(2L, 2, 1500);
		OrderDetailCommand orderDetail3 = new OrderDetailCommand(3L, 5, 3000);
		orderDetailCommands.add(orderDetail1);
		orderDetailCommands.add(orderDetail2);
		orderDetailCommands.add(orderDetail3);
		OrderCommand orderCommand  = new OrderCommand(1L,orderDetailCommands);
		
		when(orderRepository.save(any(Order.class)))
	    .thenAnswer(invocation -> {
	        Order savedOrder = invocation.getArgument(0);
	        ReflectionTestUtils.setField(savedOrder, "id", 1L); // id 설정
	        return savedOrder;
	    });

		
		// when
		Order resultOrder = orderService.createOrder(orderCommand);
		
		// then
		assertThat(resultOrder.getTotalPrice()).isEqualTo(21000);
		verify(orderDetailRepository, times(3)).save(any());
	}
}
