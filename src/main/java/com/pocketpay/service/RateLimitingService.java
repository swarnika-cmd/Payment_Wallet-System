package com.pocketpay.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, this::createNewBucket);
    }

    private Bucket createNewBucket(String key) {
        // Limit: 100 requests per minute
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
