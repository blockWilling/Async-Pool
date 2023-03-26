package com.blockwilling.controller.async;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by blockWilling on 2022/7/29.
 */
@RestController
@RequestMapping("/user/list")
public class Demo {
    @GetMapping("/list")
    public AsyncResult list(int a) {
        try {
            Thread.sleep(a * 1000);
            return new AsyncResult("suc" + a);
        } catch (Exception e) {
            e.getMessage();
            return new AsyncResult("err:" + e.getMessage());
        }
    }

    @Data
    public static class AsyncResult {
        String ret;
        public AsyncResult(String ret) {
            this.ret = ret;
        }


    }
}
