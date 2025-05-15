package kr.hhplus.be.server.infrastructure.order;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.order.dto.TopSellingProduct;
import kr.hhplus.be.server.domain.order.entity.OrderDetail;
import kr.hhplus.be.server.domain.order.repository.OrderRedisRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRedisRepositoryImpl implements OrderRedisRepository{
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void increaseProductScore(OrderDetail orderDetail) {
		String key = "product:ranking:"+LocalDate.now();
		redisTemplate.opsForZSet().incrementScore(key, String.valueOf(orderDetail.getProductId()), orderDetail.getQuantity());
		
		// 최초 TTL 설정
		Long expire = redisTemplate.getExpire(key);
		if(expire == null || expire == -1) {
			redisTemplate.expire(key, Duration.ofDays(5));
		}
	}

	@Override
	public List<TopSellingProduct> productRanking(LocalDateTime fromDate) {
		LocalDate fromDay = fromDate.toLocalDate();
		LocalDate todayMinus = LocalDate.now().minusDays(1);
		
		String resultKey = "product:ranking:"+fromDay+"-"+todayMinus;
		String fromKey = "product:ranking:"+fromDay;
		List<String> unionKeys = new ArrayList<String>();
		
		for(LocalDate d=fromDay.plusDays(0); !d.isAfter(todayMinus); d=d.plusDays(1)) {
			String unionKey = "product:ranking:"+d;
			unionKeys.add(unionKey);
		}
		
		redisTemplate.opsForZSet().unionAndStore(fromKey, unionKeys, resultKey);
		
		// 최초 TTL 설정
		Long expire = redisTemplate.getExpire(resultKey);
		if(expire == null || expire == -1) {
			redisTemplate.expire(resultKey, Duration.ofDays(1));
		}
		
		Set<TypedTuple<Object>> ranking = redisTemplate.opsForZSet().reverseRangeWithScores(resultKey, 0, -1);
		return ranking.stream().map(r -> new TopSellingProduct(Long.parseLong(r.getValue().toString()), r.getScore().longValue()))
				.collect(Collectors.toList());
	}
}
