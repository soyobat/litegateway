package com.grace.gateway.core.filter.cors;

import com.grace.gateway.common.enums.ResponseCode;
import com.grace.gateway.core.context.GatewayContext;
import com.grace.gateway.core.filter.Filter;
import com.grace.gateway.core.helper.ContextHelper;
import com.grace.gateway.core.helper.ResponseHelper;
import com.grace.gateway.core.response.GatewayResponse;
import io.netty.handler.codec.http.HttpMethod;

import static com.grace.gateway.common.constant.FilterConstant.CORS_FILTER_NAME;
import static com.grace.gateway.common.constant.FilterConstant.CORS_FILTER_ORDER;

public class CorsFilter implements Filter {

    @Override
    public void doPreFilter(GatewayContext context) {
        /**
         * 📌 OPTIONS 的作用
         *
         * 查询服务器支持的 HTTP 方法
         *
         * 例如：客户端可以用 OPTIONS 请求某个 URL，服务器会返回该 URL 可以使用的请求方法（GET、POST、PUT...）。
         *
         * 类似“先问一下你能做什么”。
         *
         * CORS 跨域预检请求（最常见）
         *
         * 当浏览器要跨域发送“复杂请求”时（比如 POST 携带 JSON、或者有自定义 header），会先自动发一个 OPTIONS 请求给目标服务器。
         *
         * 这个请求里会问：
         *
         * 你允许我这个源（Origin）访问吗？
         *
         * 你支持哪些方法？
         *
         * 你允许哪些请求头？
         *
         * 如果服务端返回 OK（带上 Access-Control-Allow-* 的响应头），浏览器才会继续发真正的请求（如 POST）。
         */
        if (HttpMethod.OPTIONS.equals(context.getRequest().getMethod())) {
            context.setResponse(ResponseHelper.buildGatewayResponse(ResponseCode.SUCCESS));
            ContextHelper.writeBackResponse(context);
        } else {
            context.doFilter();
        }
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        GatewayResponse gatewayResponse = context.getResponse();
        gatewayResponse.addHeader("Access-Control-Allow-Origin", "*");
        gatewayResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        gatewayResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        gatewayResponse.addHeader("Access-Control-Allow-Credentials", "true");
        context.doFilter();
    }

    @Override
    public String mark() {
        return CORS_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return CORS_FILTER_ORDER;
    }

}
