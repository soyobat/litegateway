package com.grace.gateway.user.caffeine;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Builder;

/**
 * @author: lkl
 * @date: 2025/10/18 1:10
 */
public class LocalCache<K,V>{
    private final Cache<K,V> cache;
    public LocalCache() {
        cache = Caffeine.newBuilder()
            .maximumSize(100)
            .build();
    }
    public void put(K key, V value, Duration timeout) {
        cache.policy().expireVariably().ifPresent(policy -> policy.put(key, value, timeout));
    }
}
