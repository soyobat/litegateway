package com.grace.gateway.config.helper;

import com.grace.gateway.common.enums.ResponseCode;
import com.grace.gateway.common.exception.NotFoundException;
import com.grace.gateway.config.manager.DynamicConfigManager;
import com.grace.gateway.config.pojo.RouteDefinition;

import java.util.*;
import java.util.regex.Pattern;

public class RouteResolver {

    private static final DynamicConfigManager manager = DynamicConfigManager.getInstance();

    /**
     * 根据uri解析出对应的路由
     */
    /**
     * 把所有路由模式转成正则
     *
     * 找出所有匹配请求 URI 的路由
     *
     * 如果没有匹配 → 抛 404 异常
     *
     * 如果有多个匹配 → 按 order 和 URI 长度选出最优一个
     *
     * 返回最优匹配的 RouteDefinition
     * @param uri
     * @return
     */
    public static RouteDefinition matchingRouteByUri(String uri) {
        Set<Map.Entry<String, RouteDefinition>> allUriEntry = manager.getAllUriEntry();

        List<RouteDefinition> matchedRoute = new ArrayList<>();

        for (Map.Entry<String, RouteDefinition> entry: allUriEntry) {
            String regex = entry.getKey().replace("**", ".*");
            if (Pattern.matches(regex, uri)) {
                matchedRoute.add(entry.getValue());
            }
        }
        if (matchedRoute.isEmpty()) {
            throw new NotFoundException(ResponseCode.PATH_NO_MATCHED);
        }

        matchedRoute.sort(Comparator.comparingInt(RouteDefinition::getOrder));

        return matchedRoute.stream()
                .min(Comparator.comparingInt(RouteDefinition::getOrder)
                        .thenComparing(route -> route.getUri().length(), Comparator.reverseOrder()))
                .orElseThrow();
    }

}
