## Day 12: The Rate Limiter (Token Bucket)

**Objective:** Protect your system from abuse.
**The Prompt:** Design a Rate Limiter core that allows ** requests per second** per user.

**The Algorithm: Token Bucket**

* Imagine a bucket that holds `Capacity` tokens (e.g., 10).
* Every `RefillRate` seconds (e.g., 1 second), we add 1 token to the bucket.
* **Logic:**
* User Request -> Check Bucket.
* If Bucket > 0 -> Remove 1 token, Allow.
* If Bucket == 0 -> Deny (Return 429).



**The "Gauntlet" Constraints:**

1. **Lazy Refill (Crucial):** Do **not** create a background thread that wakes up every second to add tokens. That is inefficient for millions of users.
* *Hint:* Refill tokens **only when the user makes a request**.
* *Math:* `newTokens = (currentTime - lastRefillTime) * refillRate`.


2. **Thread Safety:** If a user sends 100 concurrent requests, the token calculation must be atomic.
3. **No Libraries:** Do not use Google Guava or similar. Write the math yourself.

**The Assignment:**
Write a class `RateLimiter` with a method `boolean allowRequest(String userId)`.

* **Capacity:** 10.
* **Refill Rate:** 1 token per second.

**Go.**



You are absolutely right. Using `synchronized` on the `allowRequest` method creates a "Stop-the-World" bottleneck for that specific object.

In a high-throughput scenario (e.g., thousands of requests per second), threads will queue up waiting for the lock. This causes:

1. **Thread Contention:** High context-switching overhead.
2. **Latency Spikes:** Requests at the back of the queue wait longer simply to check a boolean.

### The Challenge: Updating Two Variables

The reason we used `synchronized` is that we need to update **two** variables atomically:

1. `currentTokens`
2. `lastRefillTimestamp`

If you just used separate `AtomicDouble` and `AtomicLong` variables, you would run into race conditions (e.g., thread A updates the tokens, but before it updates the timestamp, Thread B reads the old timestamp and "double refills").

### The Solution: Lock-Free CAS (Compare-And-Swap)

To make this lock-free, we can treat the state (tokens + timestamp) as a single **Immutable Object** and swap it atomically using an `AtomicReference`.

Here is the "Senior Engineer" implementation using **Compare-And-Swap (CAS)**. This implementation is non-blocking and thread-safe.

```java
package RateLimiter;

import java.util.concurrent.atomic.AtomicReference;

interface RateLimiter {
    boolean allowRequest(int tokens);
}

class LockFreeTokenBucket implements RateLimiter {
    private final double capacity;
    private final double refillRate; // tokens per second
    
    // Holds the entire state in one atomic reference
    private final AtomicReference<BucketState> state;

    // Immutable helper class to hold the state
    private static class BucketState {
        final double tokens;
        final long lastRefillTime;

        BucketState(double tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

    public LockFreeTokenBucket(double capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.state = new AtomicReference<>(new BucketState(capacity, System.nanoTime()));
    }

    @Override
    public boolean allowRequest(int cost) {
        while (true) {
            // 1. Read current state
            BucketState current = state.get();
            
            // 2. Calculate new state locally (Pure function, no side effects)
            long now = System.nanoTime();
            double elapsedTimeSeconds = (now - current.lastRefillTime) / 1_000_000_000.0;
            
            double newTokens = Math.min(capacity, current.tokens + (elapsedTimeSeconds * refillRate));
            
            // 3. Check if we have enough tokens
            if (newTokens < cost) {
                return false; // Fail fast, no need to update state
            }
            
            // 4. Create the new proposed state
            BucketState next = new BucketState(newTokens - cost, now);
            
            // 5. CAS: Attempt to swap Current -> Next
            // If another thread updated 'state' while we were calculating, this fails.
            // The loop will restart, re-read the new state, and try again.
            if (state.compareAndSet(current, next)) {
                return true;
            }
            // If CAS failed, loop repeats immediately
        }
    }
}

public class App {
    public static void main(String[] args) throws InterruptedException {
        // ... Test harness ...
        LockFreeTokenBucket limiter = new LockFreeTokenBucket(10, 5);
        // ...
    }
}

```

### Why this is better for interviews:

1. **Zero Blocking:** No thread is ever put to sleep. If a collision occurs, the CPU just spins once more (which is strictly faster than a context switch).
2. **Immutability:** Shows you understand how to manage state safely without locks.
3. **NanoTime:** I switched to `System.nanoTime()` here because `currentTimeMillis` relies on the system clock, which can "jump" (e.g., NTP updates), whereas `nanoTime` is monotonic and safer for measuring intervals.

### When to stick with `synchronized`?

If you implement **"Token Bucket per User"**, you might have a `Map<UserId, RateLimiter>`.

* If you have 1 million users, you have 1 million `RateLimiter` objects.
* The contention is only *per user*.
* In that specific case, `synchronized` is actually fine because it's unlikely a single user is hitting their *own* limit with 50 concurrent threads simultaneously.

The lock-free approach is critical when **multiple users share the same global limit** (e.g., "Allow max 1000 requests/sec for the entire API").

Would you like to move on to the **Distributed** version (Redis/Lua), or discuss **Sliding Window Log**?