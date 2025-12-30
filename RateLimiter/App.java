package RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// 1. Define the contract
interface RateLimiter {
    boolean allowRequest();
}

class TokenBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final int refillRate; // tokens per second
    
    private double currentTokens;
    private long lastRefillTimestamp;

    public TokenBucketRateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.currentTokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        
        if (currentTokens >= 1) {
            currentTokens -= 1;
            return true;
        }
        
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        
        // Optimize: Don't calculate if no time has passed to prevent divide by zero or unnecessary math
        if (elapsed <= 0) return;

        // 2. Fix: Use double for calculation to allow sub-second granularity
        // We calculate how many tokens *would* have generated in this time window
        double tokensToAdd = (elapsed / 1000.0) * refillRate;

        if (tokensToAdd > 0) {
            currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
            // 3. Fix: Update timestamp to now to prevent time drift
            lastRefillTimestamp = now; 
        }
    }
}

public class App {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Rate Limiter Application Started");

        // Scenario: Capacity of 10, Refills 5 tokens per second.
        RateLimiter rateLimiter = new TokenBucketRateLimiter(10, 5);

        // Simulate 15 concurrent users
        ExecutorService executor = Executors.newFixedThreadPool(15);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            final int requestId = i;
            executor.submit(() -> {
                boolean allowed = rateLimiter.allowRequest();
                long currentTime = System.currentTimeMillis() - startTime;
                if (allowed) {
                    System.out.printf("[%dms] Request %d: ALLOWED%n", currentTime, requestId);
                } else {
                    System.out.printf("[%dms] Request %d: THROTTLED%n", currentTime, requestId);
                }
                
                // Simulate a small processing delay so requests don't hit at the exact same nanosecond
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Test Complete");
    }
}