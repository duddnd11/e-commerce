package kr.hhplus.be.server.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {
	
	/**
	 * 락 획득 -> 트랜잭션 시작 -> 비즈니스 로직 -> 트랜잭션 종료 -> 락 해제 순서를 보장하기 위해 트랜잭션을 새로 시작
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
