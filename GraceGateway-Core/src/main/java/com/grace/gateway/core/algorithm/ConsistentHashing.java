package com.grace.gateway.core.algorithm;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {

    private final int virtualNodeNum;

    // 哈希环
    private final SortedMap<Integer, String> hashCircle = new TreeMap<>();

    // 构造函数，初始化一致性哈希环
    public ConsistentHashing(List<String> nodes, int virtualNodeNum) {
        this.virtualNodeNum = virtualNodeNum;
        for (String node : nodes) {
            addNode(node);
        }
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodeNum; i++) {
            String virtualNode = node + "&&VN" + i;
            hashCircle.put(getHash(virtualNode), node);
        }
    }

    /**
     * 给定一个 key 的 hash，比如 keyHash = 55，我们要找：
     * 顺时针方向第一个 hash ≥ 55 的节点 → "NodeC"
     * 如果 keyHash = 90（大于最大节点 hash 80） → 环绕回 "NodeA"
     * @param key
     * @return
     */
    public String getNode(String key) {
        if (hashCircle.isEmpty()) {
            return null;
        }
        int hash = getHash(key);
        //返回一个 视图（view），包含所有 key ≥ fromKey 的条目。
        SortedMap<Integer, String> tailMap = hashCircle.tailMap(hash);
        //如果这个view不存在，就返回环的头个节点。
        Integer nodeHash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        return hashCircle.get(nodeHash);
    }

    /**
     * 基础算法：FNV-1a 32位哈希
     *
     * 增强混合：通过移位、异或和加法增加哈希分布均匀性
     * 用途：
     * 哈希表索引
     * 布隆过滤器
     * 分布式一致性哈希（配合取模或环）
     * @param str
     * @return
     */
    private int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

}