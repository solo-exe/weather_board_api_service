package com.sollo_script.weather_board_api_service.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, this::createNewBucket);
    }

    private Bucket createNewBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(2)
                .refillGreedy(5, Duration.ofSeconds(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
