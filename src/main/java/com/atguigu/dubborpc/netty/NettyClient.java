package com.atguigu.dubborpc.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    //创建线程池
    private final static ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientCallable;
    private int count = 0;

    /**
     * 获取一个代理对象
     *
     * @param serviceClass 代理目标类
     * @param providerName 协议头
     * @return 代理对象
     */
    public Object getBean(final Class<?> serviceClass, final String providerName) {

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass},
                //真正代理逻辑，客户端每调用一次 hello方法, 就会进入到此处{}的代码
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("InvocationHandler.invoke 进入...." + (++count) + " 次");

                        //若客户端未启动，则先初始化并启动客户端
                        if (clientCallable == null) {
                            initClient();
                        }

                        //设置要发给服务器端的信息
                        //providerName是协议头， args[0] 就是客户端调用 hello(?) 中传入的参数
                        clientCallable.setParam(providerName + args[0]);

                        //提交到线程池执行并等待结果返回
                        return executor.submit(clientCallable).get();
                    }
                });
    }

    /**
     * 初始化并启动客户端
     */
    private static void initClient() {
        clientCallable = new NettyClientHandler();
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(clientCallable);
                            }
                        }
                );
        try {
            bootstrap.connect("127.0.0.1", 7000).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
