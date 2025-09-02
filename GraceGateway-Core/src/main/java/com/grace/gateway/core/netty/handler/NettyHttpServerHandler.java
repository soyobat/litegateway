package com.grace.gateway.core.netty.handler;

import com.grace.gateway.core.netty.processor.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 自定义请求处理器
 *
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    private final NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //把msg转成 FULLHTTPRequest ，为什么Netty要把msg 声明为Object，因为请求不一定是FullHttpRequest
        //为什么能安全地强转为 FullHttpRequest?
        //尽管 channelRead 接收的是 Object 类型，但您的代码能够安全地进行强制类型转换，是因为在 ChannelPipeline 中，HttpObjectAggregator 已经完成了它的工作。
        nettyProcessor.process(ctx, (FullHttpRequest) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 调用父类的 exceptionCaught 方法，它将按照 ChannelPipeline 中的下一个处理器继续处理异常
        super.exceptionCaught(ctx, cause);
    }

}