package com.grace.gateway.user.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: lkl
 * @date: 2025/9/13 1:51
 */
public class ThreadPool {
    public static Executor getThreadPool(){
        return Executors.newSingleThreadExecutor();
    }
}
