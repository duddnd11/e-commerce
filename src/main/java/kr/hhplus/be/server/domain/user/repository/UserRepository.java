package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.User;

public interface UserRepository {
	User save(User user);
	User findById(Long userId);
}
