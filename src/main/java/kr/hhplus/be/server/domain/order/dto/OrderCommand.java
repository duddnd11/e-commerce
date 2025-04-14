package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCommand {
	private long userId;
	
	private List<OrderDetailCommand> orderDetailCommands;
	
}
