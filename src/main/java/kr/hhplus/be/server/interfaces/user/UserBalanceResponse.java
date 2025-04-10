package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
	private long userId;
	private long balance;
	
	public static UserBalanceResponse from(User user) {
		UserBalanceResponse response = new UserBalanceResponse();
		response.userId = user.getId();
		response.balance = user.getBalance();
		return response;
	}
}
