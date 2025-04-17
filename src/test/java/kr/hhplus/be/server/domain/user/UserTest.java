package kr.hhplus.be.server.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

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
	@DisplayName("포인트 충전 동시성 테스트")
	void chargeConcurrent() throws InterruptedException {
		// given
		User user = new User("이름");
		int amount = 1000;
		
		int numberOfThread = 1000;
		CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
		CyclicBarrier barrier = new CyclicBarrier(numberOfThread);
		
		// when
		for(int i=0; i<numberOfThread; i++) {
			new Thread(() -> {
				try {
	                barrier.await(); // 전부 여기 모일 때까지 대기 → 동시에 출발
	                user.chargeBalance(amount);
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                countDownLatch.countDown();
	            }
            }).start();
		}
		
		countDownLatch.await();
		
		// then
		assertThat(user.getBalance()).isEqualTo(1000000);
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
	@DisplayName("잔액 사용 동시성 테스트")
	void usePointConcurrent() throws InterruptedException {
	    // given
	    User user = new User("이름");
	    user.chargeBalance(1000000);
	    int amount = 100;

	    int numberOfThread = 1000;
	    CountDownLatch countDownLatch = new CountDownLatch(numberOfThread);
	    CyclicBarrier barrier = new CyclicBarrier(numberOfThread);

	    // when
	    for (int i = 0; i < numberOfThread; i++) {
	        new Thread(() -> {
	            try {
	                barrier.await(); // 전부 여기 모일 때까지 대기 → 동시에 출발
	                user.useBalance(amount); // 잔액 차감
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                countDownLatch.countDown();
	            }
	        }).start();
	    }

	    countDownLatch.await(); // 모든 스레드가 끝날 때까지 대기

	    // then
	    assertThat(user.getBalance()).isEqualTo(900000); // 잔액이 덜 차감되었을 수 있음
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
