package kr.hhplus.be.server.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import kr.hhplus.be.server.domain.product.DeductStockCommand;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;

public class ProductServiceTest {

	@Mock
	ProductRepository productRepository;

	@InjectMocks
	ProductService productService;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito 초기화
	}
	
	@Test
	@DisplayName("상품 조회")
	void getProduct() {
		// given
		Product product = new Product("모니터", 100000, 100);
		when(productRepository.findById(1L)).thenReturn(product);
		ProductCommand productCommand = new ProductCommand(1L);
		
		// when
		Product resultProduct = productService.getProduct(productCommand);
		
		// then
		assertThat(resultProduct.getName()).isEqualTo(product.getName());
		assertThat(resultProduct.getPrice()).isEqualTo(product.getPrice());
		assertThat(resultProduct.getStock()).isEqualTo(product.getStock());
	}
	
	@Test
	@DisplayName("0이하 아이디 상품 조회 실패")
	void getProductUnderZero() {
		// given
		ProductCommand productCommand = new ProductCommand(0L);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> productService.getProduct(productCommand));
	}
	
	@Test
	@DisplayName("0이하 아이디 상품 재고 차감 실패")
	void deductProductUnderZero() {
		// given
		DeductStockCommand deductStockCommand = new DeductStockCommand(0L, 10);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> productService.deductStock(deductStockCommand));
	}
	
}
