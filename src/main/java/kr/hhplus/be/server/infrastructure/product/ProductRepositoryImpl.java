package kr.hhplus.be.server.infrastructure.product;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository{
	
	private final ProductJpaRepository jpaRepository;

	@Override
	public Product findById(long productId) {
		return jpaRepository.findById(productId).orElseThrow();
	}

	@Override
	public Product save(Product product) {
		return jpaRepository.save(product);
	}
}
