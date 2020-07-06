package com.atguigu.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 使用netty的IdleStateHandler提供的心跳机制检测客户端是否还存活，
 * 并根据不同IdleStateEvent(读空闲，写空闲，读写空闲)做出不同处理
 */
public class MyServer {
    public static void main(String[] args) throws Exception {


        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //默认cpu*2个NioEventLoop
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))  //加入INFO级别日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /*
                            加入一个netty 提供的处理空闲状态的处理器 IdleStateHandler
                            参数说明：
                                1. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                                2. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                                3. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                                4. TimeUnit unit : 时间单位
                            文档说明：triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                                当 IdleStateEvent 触发后 , 就会传递给管道的下一个handler去处理，通过调用(触发)下一个handler的 userEventTriggered , 在该方法中去处理IdleStateEvent(读空闲，写空闲，读写空闲)
                             */
                            pipeline.addLast(new IdleStateHandler(7000, 7000, 10, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler(自定义)
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
