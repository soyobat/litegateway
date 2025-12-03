package com.grace.gateway.core.helper;

import com.grace.gateway.config.helper.RouteResolver;
import com.grace.gateway.config.manager.DynamicConfigManager;
import com.grace.gateway.config.pojo.RouteDefinition;
import com.grace.gateway.core.context.GatewayContext;
import com.grace.gateway.core.request.GatewayRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ContextHelper {

    public static GatewayContext buildGatewayContext(FullHttpRequest request, ChannelHandlerContext ctx) {
        RouteDefinition route = RouteResolver.matchingRouteByUri(request.uri());

        GatewayRequest gatewayRequest = RequestHelper.buildGatewayRequest(
                DynamicConfigManager.getInstance().getServiceByName(route.getServiceName()), request, ctx);

        return new GatewayContext(ctx, gatewayRequest, route, HttpUtil.isKeepAlive(request));
    }

    public static void writeBackResponse(GatewayContext context) {
        FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(context.getResponse());
        // 保活
        if (!context.isKeepAlive()) { // 短连接
            context.getNettyCtx().writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else { // 长连接
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            context.getNettyCtx().writeAndFlush(httpResponse);
        }
        //在 Netty 中：
        //context.getNettyCtx().writeAndFlush(httpResponse);
        //writeAndFlush() 并不是立即把数据写到 TCP 连接上，而是异步地把数据放到发送缓冲区。
        //它返回一个 ChannelFuture，表示这个写操作的结果未来会完成，可能成功也可能失败。
        //所以，如果你直接在写完数据后调用 close()，可能会出现数据还没发出去，连接就被关闭的情况。这样客户端就可能收不到完整响应。
        // 故采用一个 监听器的模式
    }

}
