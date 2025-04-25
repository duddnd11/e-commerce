package kr.hhplus.be.server.config.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomRateLimitFilter extends RateLimitFilter {
    private final Map<String, Long> requestTimestampMap = new ConcurrentHashMap<>();
    private static final long REQUEST_LIMIT_TIME = 1000; // 1초 이내 요청 제한

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest servletRequest) {

            // POST, PUT, PATCH, DELETE 메서드만 처리
            if ("POST".equalsIgnoreCase(servletRequest.getMethod()) ||
                "PUT".equalsIgnoreCase(servletRequest.getMethod()) ||
                "PATCH".equalsIgnoreCase(servletRequest.getMethod()) ||
                "DELETE".equalsIgnoreCase(servletRequest.getMethod())) {

                String clientIp = servletRequest.getRemoteAddr(); // IP 기준으로 중복 요청 차단 예시
                long currentTime = System.currentTimeMillis();

                // 이전 요청 시간이 있다면 중복 요청인지 확인
                if (requestTimestampMap.containsKey(clientIp)) {
                    long lastRequestTime = requestTimestampMap.get(clientIp);
                    if (currentTime - lastRequestTime < REQUEST_LIMIT_TIME) {
                        HttpServletResponse httpResponse = (HttpServletResponse) response;
                        httpResponse.setStatus(429); // 429 Too Many Requests 
                        httpResponse.getWriter().write("중복 요청입니다.");
                        return;
                    }
                }

                // 요청 시간을 갱신
                requestTimestampMap.put(clientIp, currentTime);

                super.doFilter(request, response, chain);
                return;
            }
        }

        // POST, PUT, PATCH, DELETE 외의 요청은 그대로 통과
        chain.doFilter(request, response);
    }
}

