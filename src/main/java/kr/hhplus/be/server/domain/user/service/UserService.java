package kr.hhplus.be.server.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.user.dto.BalanceCommand;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	
	public User getUser(long userId) {
		return userRepository.findById(userId);
	}
	
	@Transactional
	public User charge(BalanceCommand balanceCommand) {
		User user = userRepository.findById(balanceCommand.getUserId());
		user.chargeBalance(balanceCommand.getAmount());
		return user;
	}
	
	public User useBalance(BalanceCommand balanceCommand) {
		User user = userRepository.findById(balanceCommand.getUserId());
		user.useBalance(balanceCommand.getAmount());
		userRepository.save(user);
		return user;
	}
}
