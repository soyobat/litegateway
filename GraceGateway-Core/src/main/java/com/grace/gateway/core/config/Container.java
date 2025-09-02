package com.grace.gateway.core.config;

import com.grace.gateway.config.config.Config;
import com.grace.gateway.core.netty.NettyHttpClient;
import com.grace.gateway.core.netty.NettyHttpServer;
import com.grace.gateway.core.netty.processor.DisruptorNettyCoreProcessor;
import com.grace.gateway.core.netty.processor.NettyCoreProcessor;
import com.grace.gateway.core.netty.processor.NettyProcessor;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.grace.gateway.common.constant.GatewayGlobalConstant.BUFFER_TYPE_PARALLEL;

/**
 * @author: lkl
 * @date: 2025/8/30 16:09
 * 1. 容器概念：在软件架构中，Container通常指代包含和管理其他组件的容器。这里Container管理着网关的核心网络组件。
 * 2. 职责明确：Container的职责是包含和协调NettyHttpServer和NettyHttpClient，这个名字准确反映了其作用。
 * 3. 设计模式：体现了容器化的设计思想，将相关的组件封装在一起，便于统一管理和操作。
 * 作用：
 * - 在构造函数中初始化NettyHttpServer和NettyHttpClient
 * - start()方法中按顺序启动服务端和客户端
 * - shutdown()方法中按顺序关闭服务端和客户端
 * - 通过AtomicBoolean确保启动和关闭操作的原子性
 */
public class Container implements LifeCycle {

    private final NettyHttpServer nettyHttpServer;

    private final NettyHttpClient nettyHttpClient;

    private final NettyProcessor nettyProcessor;

    private final AtomicBoolean start = new AtomicBoolean(false);

    public Container(Config config) {
        //如果启动要使用多生产者多消费组 那么我们读取配置
        NettyProcessor processor = BUFFER_TYPE_PARALLEL.equals(config.getParallelBufferType())
            ? new DisruptorNettyCoreProcessor(config, new NettyCoreProcessor())
            : new NettyCoreProcessor();
        this.nettyProcessor = processor;
        this.nettyHttpServer = new NettyHttpServer(config, processor);
        this.nettyHttpClient = new NettyHttpClient(config);
    }

    @Override
    public void start() {
        if (!start.compareAndSet(false, true)) {return;}
        nettyProcessor.start();
        nettyHttpServer.start();
        nettyHttpClient.start();
    }

    @Override
    public void shutdown() {
        if (!start.get()) {return;}
        nettyProcessor.stop();
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }

    @Override
    public boolean isStarted() {
        return start.get();
    }

}
