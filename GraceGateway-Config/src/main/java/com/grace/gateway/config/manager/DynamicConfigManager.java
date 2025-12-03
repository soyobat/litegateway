package com.grace.gateway.config.manager;

import com.grace.gateway.config.pojo.RouteDefinition;
import com.grace.gateway.config.pojo.ServiceDefinition;
import com.grace.gateway.config.pojo.ServiceInstance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 动态配置管理，缓存从配置中心拉取下来的配置
 *
 * DynamicConfigManager是GraceGateway的动态配置管理器，主要实现如下：
 *
 *   1. 单例模式：使用静态实例确保全局唯一性
 *   2. 数据存储：使用多个ConcurrentHashMap存储不同维度的配置数据
 *     - routeId2RouteMap：路由ID到路由定义的映射
 *     - serviceName2RouteMap：服务名到路由定义的映射
 *     - uri2RouteMap：URI路径到路由定义的映射
 *     - serviceDefinitionMap：服务定义映射
 *     - serviceInstanceMap：服务实例映射
 *   3. 核心功能：
 *     - 路由管理：更新、查询路由配置
 *     - 服务管理：更新、查询服务定义
 *     - 实例管理：添加、更新、删除服务实例
 *     - 监听机制：支持路由变化监听器
 *   4. 线程安全：使用ConcurrentHashMap和CopyOnWriteArrayList确保并发安全
 *   5. 动态更新：提供批量更新接口支持配置的动态变更
 *
 *   该管理器作为配置中心与网关核心的桥梁，维护运行时配置状态。
 *
 */
public class DynamicConfigManager {

    private static final DynamicConfigManager INSTANCE = new DynamicConfigManager();
    // 路由规则变化监听器
    private final ConcurrentHashMap<String /* 服务名 */, List<RouteListener>> routeListenerMap = new ConcurrentHashMap<>();
    // 路由id对应的路由
    private final ConcurrentHashMap<String /* 路由id */, RouteDefinition> routeId2RouteMap = new ConcurrentHashMap<>();
    // 服务对应的路由
    private final ConcurrentHashMap<String /* 服务名 */, RouteDefinition> serviceName2RouteMap = new ConcurrentHashMap<>();
    // URI对应的路由
    private final ConcurrentHashMap<String /* URI路径 */, RouteDefinition> uri2RouteMap = new ConcurrentHashMap<>();
    // 服务
    private final ConcurrentHashMap<String /* 服务名 */, ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();
    // 服务对应的实例
    private final ConcurrentHashMap<String /* 服务名 */, ConcurrentHashMap<String /* 实例id */, ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();

    /*********   单例   *********/
    private DynamicConfigManager() {
    }

    public static DynamicConfigManager getInstance() {
        return INSTANCE;
    }

    /*********   路由   *********/
    public void updateRouteByRouteId(String id, RouteDefinition routeDefinition) {
        routeId2RouteMap.put(id, routeDefinition);
    }

    public void updateRoutes(Collection<RouteDefinition> routes) {
        updateRoutes(routes, false);
    }

    public void updateRoutes(Collection<RouteDefinition> routes, boolean clear) {
        if (routes == null || routes.isEmpty()) return;
        if (clear) {
            routeId2RouteMap.clear();
            serviceName2RouteMap.clear();
            uri2RouteMap.clear();
        }
        for (RouteDefinition route : routes) {
            if (route == null) continue;
            routeId2RouteMap.put(route.getId(), route);
            serviceName2RouteMap.put(route.getServiceName(), route);
            uri2RouteMap.put(route.getUri(), route);
        }
    }

    public RouteDefinition getRouteById(String id) {
        return routeId2RouteMap.get(id);
    }

    public RouteDefinition getRouteByServiceName(String serviceName) {
        return serviceName2RouteMap.get(serviceName);
    }

    public Set<Map.Entry<String, RouteDefinition>> getAllUriEntry() {
        return uri2RouteMap.entrySet();
    }

    /*********   服务   *********/
    public void updateService(ServiceDefinition serviceDefinition) {
        serviceDefinitionMap.put(serviceDefinition.getServiceName(), serviceDefinition);
    }

    public ServiceDefinition getServiceByName(String name) {
        return serviceDefinitionMap.get(name);
    }

    /*********   实例   *********/
    public void addServiceInstance(String serviceName, ServiceInstance instance) {
        serviceInstanceMap.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>()).put(instance.getInstanceId(), instance);
    }

    public void updateInstances(ServiceDefinition serviceDefinition, Set<ServiceInstance> newInstances) {
        ConcurrentHashMap<String, ServiceInstance> oldInstancesMap = serviceInstanceMap.computeIfAbsent(serviceDefinition.getServiceName(), k -> new ConcurrentHashMap<>());
        oldInstancesMap.clear();
        for (ServiceInstance newInstance : newInstances) {
            oldInstancesMap.put(newInstance.getInstanceId(), newInstance);
        }
    }

    public void removeServiceInstance(String serviceName, ServiceInstance instance) {
        serviceInstanceMap.compute(serviceName, (k, v) -> {
            if (v == null || v.get(instance.getInstanceId()) == null) return v;
            v.remove(instance.getInstanceId());
            return v;
        });
    }

    public Map<String, ServiceInstance> getInstancesByServiceName(String serviceName) {
        return serviceInstanceMap.get(serviceName);
    }

    /*********   监听   *********/
    /**
     * @param serviceName
     * @param listener
     */
    public void addRouteListener(String serviceName, RouteListener listener) {
        routeListenerMap.computeIfAbsent(serviceName, key -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void changeRoute(RouteDefinition routeDefinition) {
        List<RouteListener> routeListeners = routeListenerMap.get(routeDefinition.getServiceName());
        if (routeListeners == null || routeListeners.isEmpty()) return;
        for (RouteListener routeListener : routeListeners) {
            routeListener.changeOnRoute(routeDefinition);
        }
    }

}
