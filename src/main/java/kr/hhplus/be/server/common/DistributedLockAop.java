package kr.hhplus.be.server.common;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 높은 우선순위 설정
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
	private static final String REDISSON_LOCK_PREFIX = "LOCK:";
	
	private final RedissonClient redissonClient;
	
	@Around("@annotation(kr.hhplus.be.server.common.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
		
        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);  // (1)
		
        boolean available = false;
		try {
			log.info("[{}] 락 대기 중: {}", Thread.currentThread().getName(), key);
            available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            log.info("[{}] 락 획득 여부: {} (key: {})", Thread.currentThread().getName(), available, key);
            if (!available) {
                return false;
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
        	if(available) {
        		try {
        			rLock.unlock();
        			log.info("[{}] 락 해제 완료: {}", Thread.currentThread().getName(), key);
        		} catch (IllegalMonitorStateException e) {
        			log.info("락 해제 실패");
        		}
        	}
        }
    }
}
