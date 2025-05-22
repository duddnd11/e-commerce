package kr.hhplus.be.server.infrastructure.order;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.order.DataPlatform;

@Component
public class DataPlatFormClient implements DataPlatform{

	@Override
	public boolean send(long orderId) {
		return true;
	}
	
}
