package com.grace.gateway.core.netty.processor;

import com.grace.gateway.common.enums.ResponseCode;
import com.grace.gateway.common.exception.GatewayException;
import com.grace.gateway.core.context.GatewayContext;
import com.grace.gateway.core.filter.FilterChainFactory;
import com.grace.gateway.core.helper.ContextHelper;
import com.grace.gateway.core.helper.ResponseHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            //构建上下文
            GatewayContext gatewayContext = ContextHelper.buildGatewayContext(request, ctx);
            //从过滤器链中构建 过滤器链
            FilterChainFactory.buildFilterChain(gatewayContext);

            //开始处理
            gatewayContext.doFilter();

        } catch (GatewayException e) {
            //报错处理，像NotFoundException....这些继承了GatewayException，只要抛了异常就会被这里捕获到，然后塞到返回请求里，重新返回回去
            log.error("处理错误 {} {}", e.getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("处理未知错误", t);
            FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }
    }

    @Override
    public void start() {
        //
    }

    @Override
    public void stop() {
        //
    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        //  1. write()：将数据写入到Netty的发送缓冲区中，但此时数据还没有真正发送到网络
        //  2. flush()：强制将缓冲区中的数据立即发送到网络中
        //  为什么要刷新（flush）?
        //  1. 缓冲机制：Netty为了提高性能，使用了缓冲机制。数据先写入缓冲区，而不是立即发送
        //  2. 批量发送：通过缓冲可以将多个小数据包合并成一个大的数据包发送，减少网络IO次数
        //  3. 及时响应：在网关场景中，处理完请求后需要立即将响应返回给客户端，不能等待缓冲区满才发送

        //响应写回：将构建好的HTTP响应通过Netty通道写回给客户端
        ctx.writeAndFlush(httpResponse)
            /**
             *              发送响应后关闭通道
             *               1. 添加监听器的作用：
             *               ctx.writeAndFlush(httpResponse)
             *                   .addListener(ChannelFutureListener.CLOSE);
             *
             *               2. 关闭过程：
             *
             *               1. writeAndFlush执行：将响应数据写入网络并发送
             *               2. 操作完成：当数据发送完成后，Netty会标记这个操作为完成
             *               3. 触发监听器：操作完成后，Netty自动调用ChannelFutureListener.CLOSE
             *               4. 执行关闭：CLOSE监听器内部执行channel.close()操作
             *
             *               3. 实际关闭步骤：
             *
             *               // ChannelFutureListener.CLOSE的内部实现类似于：
             *               public static final ChannelFutureListener CLOSE = new ChannelFutureListener() {
             *                   @Override
             *                   public void operationComplete(ChannelFuture future) {
             *                       future.channel().close(); // 实际关闭通道
             *                   }
             *               };
             *
             *               4. 通道关闭的详细过程：
             *
             *               1. 关闭输出流：通知对方不再发送数据
             *               2. 关闭输入流：停止接收对方数据
             *               3. 释放资源：释放通道占用的内存和文件描述符
             *               4. 触发事件：触发channelInactive和channelUnregistered事件
             *
             *               5. 为什么要这样关闭：
             *
             *               1. 异步特性：Netty是异步框架，writeAndFlush也是异步操作
             *               2. 确保发送完成：只有在数据真正发送完成后才关闭连接
             *               3. 避免数据丢失：防止响应数据还未发送完就关闭连接
             *
             *               这种设计确保了响应能够完整发送给客户端后再关闭连接，是Netty中标准的连接管理方式。
             */
            .addListener(ChannelFutureListener.CLOSE);
        // 释放与请求相关联的资源
        /**
         *   1. 什么是引用计数：
         *   - Netty使用引用计数来管理内存资源
         *   - 每个ByteBuf（Netty的字节缓冲区）都有一个引用计数器
         *   - 当计数器为0时，Netty会自动释放该对象占用的内存
         */
        //FullHttpRequest 里使用到了 bytebuff
        if (request.refCnt() > 0) {
            ReferenceCountUtil.release(request);
        }
    }

}