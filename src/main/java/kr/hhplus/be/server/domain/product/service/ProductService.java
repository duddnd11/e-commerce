package kr.hhplus.be.server.domain.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.dto.StockCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	
	public Product getProduct(ProductCommand productCommand) {
		if(productCommand.getProductId() <= 0) {
			throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
		}
		return productRepository.findById(productCommand.getProductId());
	}
	
	public List<ProductResult> deductStock(List<StockCommand> stockCommands) {
		List<ProductResult> productResults = new ArrayList<ProductResult>();
		for(StockCommand stockCommand : stockCommands) {
			if(stockCommand.getProductId() <= 0) {
				throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
			}
			Product product = productRepository.findByIdForUpdate(stockCommand.getProductId());
			product.deductStock(stockCommand.getQuantity());
			productResults.add(ProductResult.of(product.getId(), stockCommand.getQuantity(), product.getPrice()));
		}
		return productResults;
	}
	
	public void addStock(List<StockCommand> stockCommands) {
		for(StockCommand stockCommand : stockCommands) {
			if(stockCommand.getProductId() <= 0) {
				throw new IllegalArgumentException("0 이하의 값을 사용할 수 없습니다.");
			}
			Product product = productRepository.findByIdForUpdate(stockCommand.getProductId());
			product.addStock(stockCommand.getQuantity());
		}
	}
}
