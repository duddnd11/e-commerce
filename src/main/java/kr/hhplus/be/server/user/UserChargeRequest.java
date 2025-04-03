package kr.hhplus.be.server.user;

import lombok.Getter;

@Getter
public class UserChargeRequest {
	private long userId;
	private int amount;
}
