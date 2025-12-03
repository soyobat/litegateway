package com.grace.gateway.user.service;

import org.springframework.stereotype.Service;

/**
 * @author: lkl
 * @date: 2025/9/13 1:49
 */
@Service
public class MyService {
    public String sayhello(){
        System.out.println("Hello World!");
        
        return "Hello World!";
    }
}
