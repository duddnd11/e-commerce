package kr.hhplus.be.server.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="User API", description = "유저 관리 (잔액 조회 및 충전)")
@RestController
@RequestMapping("/user")
public class UserController {
	
	/**
	 * 유저 잔액 조회
	 * @param userId
	 * @return
	 */
	@Operation(summary = "유저 잔액 조회")
	@GetMapping("/balance/{userId}")
	public ResponseEntity<UserBalanceResponse> getUserBalance(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(new UserBalanceResponse(userId, 1000L));
	}
	
	/**
	 * 유저 잔액 충전
	 * @param chargeRequest
	 * @return
	 */
	@Operation(summary = "유저 잔액 충전")
	@PostMapping("/charge")
	public ResponseEntity<UserBalanceResponse> charge(@RequestBody UserChargeRequest chargeRequest) {
		int userBalance = 1000;
		return ResponseEntity.ok(new UserBalanceResponse(chargeRequest.getUserId(), userBalance + chargeRequest.getAmount()));
	}
}
