package RateLimiter.myapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimitController {
    
    @GetMapping(value = "/api/v1/ratelimit")
    public ResponseEntity<String> myRatelimiter() {
        return new ResponseEntity<String>("Controller called", HttpStatus.OK);
    }
}
