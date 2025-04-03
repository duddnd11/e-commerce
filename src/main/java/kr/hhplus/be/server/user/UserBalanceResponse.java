package kr.hhplus.be.server.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
	private long userId;
	private long balance;
}
