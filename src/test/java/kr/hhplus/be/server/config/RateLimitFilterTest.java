package kr.hhplus.be.server.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitFilterTest {
	@Autowired
	CouponRepository couponRepository;
	
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("중복 요청 HTTP 429")
    void rateLimitTest() throws Exception {
    	couponRepository.save(new Coupon("쿠폰",CouponType.PERCENT,10,100));
        String jsonBody = """
            {
                "userId": 1,
                "couponId": 1
            }
        """;

        // 첫 번째 요청
        RequestBuilder request1 = MockMvcRequestBuilders
                .post("/coupon/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
        MvcResult result1 = mockMvc.perform(request1).andReturn();

        // 두 번째 요청 (1초 이내)
        RequestBuilder request2 = MockMvcRequestBuilders
                .post("/coupon/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
        MvcResult result2 = mockMvc.perform(request2).andReturn();

        MockHttpServletResponse response1 = result1.getResponse();
        MockHttpServletResponse response2 = result2.getResponse();

        // 첫 번째 요청은 성공
        assertThat(response1.getStatus()).isEqualTo(200);

        // 두 번째 요청은 중복 (429)
        assertThat(response2.getStatus()).isEqualTo(429);
    }


    @Test
    @DisplayName("정상 요청")
    void rateLimitTestSleep() throws Exception {
    	 // 정상 요청을 위한 sleep
        Thread.sleep(1500);
    	couponRepository.save(new Coupon("쿠폰",CouponType.PERCENT,10,100));
        String jsonBody = """
            {
                "userId": 1,
                "couponId": 1
            }
        """;

        // 첫 번째 요청
        RequestBuilder request1 = MockMvcRequestBuilders
                .post("/coupon/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
        MvcResult result1 = mockMvc.perform(request1).andReturn();

        // 정상 요청을 위한 sleep
        Thread.sleep(1500);
        
        // 두 번째 요청
        RequestBuilder request2 = MockMvcRequestBuilders
                .post("/coupon/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody);
        MvcResult result2 = mockMvc.perform(request2).andReturn();

        MockHttpServletResponse response1 = result1.getResponse();
        MockHttpServletResponse response2 = result2.getResponse();

        // 첫 번째 요청 성공
        assertThat(response1.getStatus()).isEqualTo(200);

        
        // 두 번째 요청 성공
        assertThat(response2.getStatus()).isEqualTo(200);

    }
}