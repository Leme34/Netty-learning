package com.luban.comsumer;

import com.luban.framework.ProxyFactory;
import com.luban.provider.api.HelloService;

public class Consumer {

    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        for (;;) {
            try {
                String result = helloService.sayHello("周瑜");
                System.out.println(result);
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }

    }
}
