package kr.hhplus.be.server.interfaces.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserChargeRequest {
	private long userId;
	private int amount;
}
