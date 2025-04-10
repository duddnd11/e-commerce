package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private ProductRepository productRepository;
	
	public Product getProduct(ProductCommand productCommand) {
		if(productCommand.getProductId() <= 0) {
			throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
		}
		return productRepository.findById(productCommand.getProductId());
	}
	
	public Product deductStock(DeductStockCommand deductStockCommand) {
		if(deductStockCommand.getProductId() <= 0) {
			throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
		}
		Product product = productRepository.findById(deductStockCommand.getProductId());
		product.deductStock(deductStockCommand.quantity);
		return productRepository.save(product);
	}
}
