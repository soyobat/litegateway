package com.grace.gateway.user.controller;


import java.util.concurrent.atomic.LongAdder;

import com.grace.gateway.user.service.MyService;
import com.grace.gateway.user.service.SpringContextHolder;
import com.grace.gateway.user.service.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class UserController {
    LongAdder longAdder = new LongAdder();
    @GetMapping("/api/user/ping1")
    public String ping1() {
        log.info("请求-"+longAdder.intValue());
        longAdder.increment();
        return "this is user ping1";
    }

    @GetMapping("/api/user/ping2")
    public String ping2() {
        ThreadPool.getThreadPool().execute(() -> {
            while(true){
                MyService myService = SpringContextHolder.getBean(MyService.class);
                myService.sayhello();
            }
        });
        return "this is user ping2";
    }

}
