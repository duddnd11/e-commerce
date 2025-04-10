package kr.hhplus.be.server.domain.product;

public interface ProductRepository {
	Product findById(long productId);
	Product save(Product product);
}
