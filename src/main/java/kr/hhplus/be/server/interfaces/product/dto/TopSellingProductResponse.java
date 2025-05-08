package kr.hhplus.be.server.interfaces.product.dto;

import kr.hhplus.be.server.application.product.dto.TopSellingProductInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSellingProductResponse {
	private Long productId;
    private Long totalQuantity;
    
    public static TopSellingProductResponse from(TopSellingProductInfo topSellingProductInfo) {
    	TopSellingProductResponse topSellingProductResponse = new TopSellingProductResponse(
    			topSellingProductInfo.getProductId(), topSellingProductInfo.getTotalQuantity());
    	return topSellingProductResponse;
    }
}
