package com.grace.gateway.user.caffeine;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.grace.gateway.user.Main;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * @author: lkl
 * @date: 2025/9/26 22:50
 */
public class Test {
    @SneakyThrows
    public static void main(String[] args){
        Cache<String, Integer> cache =
            Caffeine.newBuilder()
                .maximumSize(1000)
                .recordStats()
                //                        .expireAfterWrite(5, TimeUnit.SECONDS)
                //                        .expireAfterAccess(2, TimeUnit.SECONDS)
                .expireAfter(new Expiry<String, Integer>() {
                    @Override
                    public long expireAfterCreate(@NonNull String key, @NonNull Integer value, long currentTime) {
                        return currentTime;
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull String key, @NonNull Integer value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(@NonNull String key, @NonNull Integer value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }
                })
                .removalListener(new RemovalListener<String, Integer>() {
                    @Override
                    public void onRemoval(@Nullable String key, @Nullable Integer value, @NonNull RemovalCause cause) {

                        System.out.println("移除了key：" + key + " value :" + value + " cause : " + cause);
                    }
                })
                .build();
        System.out.println(200 & 0);
        cache.put("cliffcw1", 1);
        System.out.println(cache.getIfPresent("cliffcw1"));

        //可变过期时间策略有没有提供，如果有，就put，
        cache.policy().expireVariably().ifPresent(policy -> {
            policy.put("cliffcw2", 2, 13, TimeUnit.SECONDS);
            policy.put("cliffcw3", 2, 10, TimeUnit.SECONDS);
        });

        System.out.println("cliffcw2:" + cache.getIfPresent("cliffcw2"));

        Thread.sleep(11000);

        System.out.println("cliffcw22:" + cache.getIfPresent("cliffcw2"));
        System.out.println("cliffcw3:" + cache.getIfPresent("cliffcw3"));

    }
}
