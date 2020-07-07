package com.atguigu.dubborpc.provider;

import com.atguigu.dubborpc.api.HelloService;

import java.util.concurrent.atomic.AtomicInteger;

public class HelloServiceImpl implements HelloService {

    private static AtomicInteger count = new AtomicInteger();

    //当有消费方调用该方法时，就返回一个结果
    @Override
    public String hello(String msg) {
        System.out.println("收到客户端消息=" + msg);
        //根据 msg 返回不同的结果
        if (msg != null) {
            return "你好客户端, 我已经收到你的消息 [" + msg + "] 第" + count.getAndIncrement() + " 次";
        } else {
            return "你好客户端, 我已经收到你的消息";
        }
    }
}
