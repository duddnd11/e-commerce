package kr.hhplus.be.server.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.config.Interceptor.ApiLogInterceptor;

public class InterceptorTest {
	
	@InjectMocks
    private ApiLogInterceptor interceptor;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    void preHandle() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = new Object();

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/balance/1");

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertTrue(result);  // preHandle은 true를 반환해야 함
    }

    @Test
    void afterCompletion() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = new Object();

        when(request.getRequestURI()).thenReturn("/balance/1");
        when(response.getStatus()).thenReturn(200);

        // when
        interceptor.afterCompletion(request, response, handler, null);
    }

}
