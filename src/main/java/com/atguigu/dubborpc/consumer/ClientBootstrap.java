package com.atguigu.dubborpc.consumer;

import com.atguigu.dubborpc.netty.NettyClient;
import com.atguigu.dubborpc.api.HelloService;

import java.util.concurrent.TimeUnit;

public class ClientBootstrap {


    //这里定义协议头
    public static final String PROVIDER_NAME = "HelloService#hello#";


    public static void main(String[] args) throws  Exception{

        //创建一个消费者
        NettyClient customer = new NettyClient();

        //创建代理对象
        HelloService service = (HelloService) customer.getBean(HelloService.class, PROVIDER_NAME);

        for (;; ) {
            TimeUnit.SECONDS.sleep(2);
            //通过代理对象调用服务提供者的方法(服务)
            String res = service.hello("你好 dubbo~");
            System.out.println("调用的结果 res= " + res);
        }
    }
}
