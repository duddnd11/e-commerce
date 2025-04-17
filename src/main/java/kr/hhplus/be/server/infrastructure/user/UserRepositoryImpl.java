package kr.hhplus.be.server.infrastructure.user;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{
	private final UserJpaRepository jpaRepository;

	@Override
	public User save(User user) {
		return jpaRepository.save(user);
	}

	@Override
	public User findById(Long userId) {
		return jpaRepository.findById(userId).orElseThrow();
	}

}
