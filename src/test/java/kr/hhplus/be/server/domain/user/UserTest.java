package kr.hhplus.be.server.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.user.entity.User;

public class UserTest {

	@Test
	@DisplayName("포인트 충전")
	void charge() {
		// given
		User user = new User("이름");
		int amount = 1000;
		
		// when
		user.chargeBalance(amount);
		
		// then
		assertThat(user.getBalance()).isEqualTo(1000);
	}
	
	@Test
	@DisplayName("0 이하 포인트 충전 실패")
	void chargeUnderZero() {
		// given
		User user = new User("이름");
		int amount = 0;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> user.chargeBalance(amount));
	}
	
	@Test
	@DisplayName("잔액 사용")
	void usePoint() {
		// given
		User user = new User("이름");
		user.chargeBalance(1000);
		int amount = 400;
		
		// when
		user.useBalance(amount);
		
		// then
		assertThat(user.getBalance()).isEqualTo(600);
	}
	
	@Test
	@DisplayName("잔액 부족")
	void usePointError() {
		// given
		User user = new User("이름");
		user.chargeBalance(300);
		int amount = 400;
		
		// when & then
		assertThrows(IllegalArgumentException.class, () -> user.useBalance(amount));
	}
	
}
