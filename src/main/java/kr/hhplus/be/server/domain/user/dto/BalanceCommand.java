package kr.hhplus.be.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceCommand {
	long userId;
	int amount;
	
	public static BalanceCommand of(long userId, int amount) {
		return new BalanceCommand(userId, amount);
	}
}
