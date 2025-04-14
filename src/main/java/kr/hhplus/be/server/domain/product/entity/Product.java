package kr.hhplus.be.server.domain.product.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private int price;
	
	private int stock;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public Product(String name, int price, int stock) {
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.createdAt = LocalDateTime.now();
	}
	
	public void deductStock(int quantity) {
		if(quantity <= 0) {
			throw new IllegalArgumentException("0 이상의 수량을 입력해 주세요.");
		}
		
		if(stock < quantity) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}
		
		
		this.stock -= quantity;
		this.updatedAt = LocalDateTime.now();
	}
}
