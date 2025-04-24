package kr.hhplus.be.server.domain.product;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.product.entity.Product;

public class ProductTest {
	
	@Test
	@DisplayName("재고 차감")
	void deductStock() {
		// given
		Product product = new Product("모니터", 100000, 100);
		
		// when
		product.deductStock(100);
		
		//then
		assertThat(product.getStock()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("재고 차감 동시성 테스트")
	void deductStockConcurrent() throws InterruptedException {
		// given
		Product product = new Product("모니터", 100000, 1000000);
		
		int numberOfThread = 1000;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		
		// when
		for(int i=0; i<numberOfThread; i++) {
			new Thread(() -> {
				try {
	                barrier.await(); // 전부 여기 모일 때까지 대기 → 동시에 출발
	                product.deductStock(100);
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                countDownLatch.countDown();
	            }
            }).start();
		}
		
		countDownLatch.await();
		
		//then
		assertThat(product.getStock()).isEqualTo(900000);
	}
	
	@Test
	@DisplayName("재고 부족")
	void deductStockError() {
		// given
		Product product = new Product("모니터", 100000, 10);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> product.deductStock(11));
	}
	
	@Test
	@DisplayName("재고 차감 수량 0 이하 에러")
	void deductStockError2() {
		// given
		Product product = new Product("모니터", 100000, 10);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> product.deductStock(0));
	}
	
	@Test
	@DisplayName("재고 추가")
	void addStock() {
		// given
		Product product = new Product("모니터", 100000, 100);
		
		// when
		product.addStock(100);
		
		//then
		assertThat(product.getStock()).isEqualTo(200);		
	}
	
	@Test
	@DisplayName("재고 추가 동시성 테스트")
	void addStockConcurrent() throws InterruptedException {
	    // given
	    Product product = new Product("모니터", 100000, 100);
	    
	    int numberOfThread = 2000;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		
		// when
		for(int i=0; i<numberOfThread; i++) {
			new Thread(() -> {
				try {
	                barrier.await(); // 전부 여기 모일 때까지 대기 → 동시에 출발
	                product.addStock(100);
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                countDownLatch.countDown();
	            }
            }).start();
		}
		
		countDownLatch.await();

	    // then
	    assertThat(product.getStock()).isEqualTo(200100);
	}

	
	@Test
	@DisplayName("재고 추가 수량 0 이하 에러")
	void addStockError() {
		// given
		Product product = new Product("모니터", 100000, 10);
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> product.addStock(0));
	}
}
