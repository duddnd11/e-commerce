package kr.hhplus.be.server.application.product.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.application.product.dto.TopSellingProductInfo;
import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductFacade {
	private final OrderService orderService;
	
	public List<TopSellingProductInfo> topSellingProduct(LocalDateTime fromDate){
		List<TopSellingProduct> topSellingProducts = orderService.topSellingProduct(fromDate);
		return topSellingProducts.stream()
				.map(tp -> TopSellingProductInfo.from(tp))
				.collect(Collectors.toList());
	}
}
