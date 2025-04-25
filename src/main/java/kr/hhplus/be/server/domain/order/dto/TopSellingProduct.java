package kr.hhplus.be.server.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopSellingProduct {
	private Long productId;
    private Long totalQuantity;

    public TopSellingProduct(Long productId, Long totalQuantity) {
        this.productId = productId;
        this.totalQuantity = totalQuantity;
    }
}
