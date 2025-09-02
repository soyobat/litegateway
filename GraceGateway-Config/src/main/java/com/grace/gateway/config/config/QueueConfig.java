package com.grace.gateway.config.config;

import com.lmax.disruptor.*;
import lombok.Data;

/**
 * @author: lkl
 * @date: 2025/8/31 0:03
 */
@Data
public class QueueConfig {
    public final static String THREAD_NAME_PREFIX = "gateway-queue-";

    private int bufferSize = 1024 * 16;

    private int processThread = Runtime.getRuntime().availableProcessors();

    private String waitStrategy = "blocking";

    /**
     * 策略模式获取等待策略
     *
     * @return
     */
    public WaitStrategy getWaitStrategy() {
        switch (waitStrategy) {
            case "blocking" -> {return new BlockingWaitStrategy();}
            case "busySpin" -> {return new BusySpinWaitStrategy();}
            case "yielding" -> {return new YieldingWaitStrategy();}
            case "sleeping" -> {return new SleepingWaitStrategy();}
            default -> {
                return new BlockingWaitStrategy();
            }
        }
    }
}
