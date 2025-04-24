package kr.hhplus.be.server.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.product.entity.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long>{
}
