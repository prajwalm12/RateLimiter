package RateLimiter.myapp.config;

import java.io.IOException;
import java.time.Duration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;

@Component
public class ApiFilter extends GenericFilterBean{
	
	Bucket tokenBucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofHours(1))))
            .build();
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		 	        
	        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
	        if (probe.isConsumed()) {
	        	
	            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));		            
	        } else {
	            long waitForRefill = probe.getNanosToWaitForRefill(); 
	            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
	            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
	              "You have exhausted your API Request Quota"); 
	            
	        }	        
	    	filterChain.doFilter(request, response);
	}
}