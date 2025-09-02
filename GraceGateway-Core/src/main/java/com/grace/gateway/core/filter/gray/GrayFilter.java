package com.grace.gateway.core.filter.gray;

import cn.hutool.json.JSONUtil;
import com.grace.gateway.config.manager.DynamicConfigManager;
import com.grace.gateway.config.pojo.RouteDefinition;
import com.grace.gateway.config.pojo.ServiceInstance;
import com.grace.gateway.config.util.FilterUtil;
import com.grace.gateway.core.context.GatewayContext;
import com.grace.gateway.core.filter.Filter;
import com.grace.gateway.core.filter.gray.strategy.GrayStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.grace.gateway.common.constant.FilterConstant.GRAY_FILTER_NAME;
import static com.grace.gateway.common.constant.FilterConstant.GRAY_FILTER_ORDER;

@Slf4j
public class GrayFilter implements Filter {

    @Override
    public void doPreFilter(GatewayContext context) {
        //校验是否开启该过滤器 或者 是否存在
        RouteDefinition.FilterConfig filterConfig = FilterUtil.findFilterConfigByName(context.getRoute().getFilterConfigs(), GRAY_FILTER_NAME);
        if (filterConfig == null) {
            filterConfig = FilterUtil.buildDefaultGrayFilterConfig();
        }
        if (!filterConfig.isEnable()) {
            return;
        }

        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();

        if (instances.stream().anyMatch(instance -> instance.isEnabled() && instance.isGray())) {
            // 存在灰度实例
            GrayStrategy strategy = selectGrayStrategy(JSONUtil.toBean(filterConfig.getConfig(), RouteDefinition.GrayFilterConfig.class));
            context.getRequest().setGray(strategy.shouldRoute2Gray(context, instances));
        } else {
            // 灰度实例都没，不走灰度
            context.getRequest().setGray(false);
        }
        /** 猜猜为什么要这么做？ 控制反转，原本执行下一个过滤器是由外部控制的
         *  控制反转的好处：
         *   1. 灵活性：每个过滤器可以决定是否继续执行、何时执行
         *   2. 可中断性：某个过滤器可以中断整个链的执行（比如权限验证失败时）
         *   3. 解耦：过滤器之间不需要知道彼此的存在
         *
         * **/
        context.doFilter();
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        context.doFilter();
    }

    @Override
    public String mark() {
        return GRAY_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return GRAY_FILTER_ORDER;
    }

    private GrayStrategy selectGrayStrategy(RouteDefinition.GrayFilterConfig grayFilterConfig) {
        return GrayStrategyManager.getStrategy(grayFilterConfig.getStrategyName());
    }

}
