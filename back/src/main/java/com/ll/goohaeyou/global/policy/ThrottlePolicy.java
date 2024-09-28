package com.ll.goohaeyou.global.policy;

import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ThrottlePolicy {
    private static final String PAYMENT_PREFIX = "payment:";

    private final RedisTemplate<String, Long> redisTemplate;

    public void checkThrottle(String username, long currentTimeMillis) {
        String key = PAYMENT_PREFIX + username;

        redisTemplate.opsForZSet().add(key, currentTimeMillis, currentTimeMillis);

        long windowStart = currentTimeMillis - TimeUnit.MINUTES.toMillis(5);
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        long requestCount = redisTemplate.opsForZSet().zCard(key);

        if (requestCount > 30) {
            throw new PaymentException.ThrottleLimitExceededException();
        }

        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }
}
