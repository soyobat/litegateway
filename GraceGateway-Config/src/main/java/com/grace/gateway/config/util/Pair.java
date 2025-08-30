package com.grace.gateway.config.util;

/**
 * @author: lkl
 * @date: 2025/8/31 0:51
 */
public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> with(A first, B second) {
        return new Pair(first, second);
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }
}
