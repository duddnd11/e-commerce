package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSellingProductInfo {
	private Long productId;
    private Long totalQuantity;
    
    public static TopSellingProductInfo from(TopSellingProduct topSellingProduct) {
    	TopSellingProductInfo topSellingProductInfo = new TopSellingProductInfo(
    			topSellingProduct.getProductId(), topSellingProduct.getTotalQuantity());
    	return topSellingProductInfo;
    }
}
