package kr.hhplus.be.server.domain.user;

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
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private int balance;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	public User(String name) {
		this.name = name;
		this.createdAt = LocalDateTime.now();
	}
	
	public void chargeBalance(int amount) {
		if(amount <=0) {
			throw new IllegalArgumentException("0 이하의 금액은 충전할 수 없습니다.");
		}
		this.balance += amount;
		this.updatedAt = LocalDateTime.now();
	}
	
	public void useBalance(int amount) {
		if(balance < amount ) {
			throw new IllegalArgumentException("잔액이 부족합니다.");
		}
		this.balance-=amount;
		this.updatedAt = LocalDateTime.now();
	}
}
