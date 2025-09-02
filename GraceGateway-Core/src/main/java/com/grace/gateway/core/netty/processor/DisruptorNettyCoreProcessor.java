package com.grace.gateway.core.netty.processor;

import com.grace.gateway.common.enums.ResponseCode;
import com.grace.gateway.config.config.Config;
import com.grace.gateway.config.util.Pair;
import com.grace.gateway.core.helper.ResponseHelper;
import com.grace.gateway.core.queue.EventListener;
import com.grace.gateway.core.queue.ParallelQueueHandler;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import static com.grace.gateway.config.config.QueueConfig.THREAD_NAME_PREFIX;

/**
 * @author: lkl
 * @date: 2025/8/30 22:40
 */
@Slf4j
public class DisruptorNettyCoreProcessor implements NettyProcessor {
    private Config config;

    /**
     * Disruptor 只是缓存依然需要使用到 Netty 核心处理器
     */
    private NettyCoreProcessor nettyCoreProcessor;

    /**
     * 处理类
     */
    private ParallelQueueHandler<Pair<ChannelHandlerContext, FullHttpRequest>> parallelQueueHandler;

    /**
     * 构造方法，初始化 DisruptorNettyCoreProcessor。
     *
     * @param config             配置信息对象。
     * @param nettyCoreProcessor Netty 核心处理器。
     */
    public DisruptorNettyCoreProcessor(Config config, NettyCoreProcessor nettyCoreProcessor) {
        this.config = config;
        this.nettyCoreProcessor = nettyCoreProcessor;

        // 使用 Disruptor 创建并配置处理队列。
        ParallelQueueHandler.Builder<Pair<ChannelHandlerContext, FullHttpRequest>> builder
            = new ParallelQueueHandler.Builder<Pair<ChannelHandlerContext, FullHttpRequest>>()
            .setBufferSize(config.getQueueConfig().getBufferSize())
            .setThreads(config.getQueueConfig().getProcessThread())
            .setProducerType(ProducerType.MULTI)
            .setNamePrefix(THREAD_NAME_PREFIX)
            .setWaitStrategy(config.getQueueConfig().getWaitStrategy())
            .setListener(new BatchEventListenerProcessor());
        this.parallelQueueHandler = builder.build();
    }

    @Override
    public void process(ChannelHandlerContext ctx, FullHttpRequest request) {
        this.parallelQueueHandler.add(new Pair(ctx, request));
    }

    @Override
    public void start() {
        parallelQueueHandler.start();
    }

    @Override
    public void stop() {
        parallelQueueHandler.shutDown();
    }

    /**
     * 监听处理类，处理从 Disruptor 处理队列中取出的事件。
     */
    public class BatchEventListenerProcessor implements EventListener<Pair<ChannelHandlerContext, FullHttpRequest>> {

        @Override
        public void onEvent(Pair<ChannelHandlerContext, FullHttpRequest> event) {
            // 使用 Netty 核心处理器处理事件。
            nettyCoreProcessor.process(event.getFirst(), event.getSecond());
        }

        @Override
        public void onException(Throwable ex, long sequence, Pair<ChannelHandlerContext, FullHttpRequest> event) {
            ChannelHandlerContext ctx = event.getFirst();
            FullHttpRequest request = event.getSecond();
            try {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request,
                    ex.getMessage(), ex);

                // 构建响应对象
                FullHttpResponse fullHttpResponse = ResponseHelper.buildHttpResponse(ResponseCode.INTERNAL_ERROR);

                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.writeAndFlush(fullHttpResponse)
                        .addListener(ChannelFutureListener.CLOSE);
                } else {
                    fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(fullHttpResponse);
                }

            } catch (Exception e) {
                log.error("BatchEventListenerProcessor onException 请求写回失败，request:{}, errMsg:{} ", request,
                    e.getMessage(), e);
            }
        }
    }
}
