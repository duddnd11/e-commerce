package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;

public interface ProductRepository {
	Product findById(long productId);
	Product save(Product product);
}
