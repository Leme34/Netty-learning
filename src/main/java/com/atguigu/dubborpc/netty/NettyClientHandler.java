package com.atguigu.dubborpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;//Handler上下文
    private String result; //返回的结果
    private String param; //客户端调用方法时，传入的参数

    //(2)
    void setParam(String param) {
        System.out.println("setPara");
        this.param = param;
    }

    /**
     * 与服务器的连接创建后会被调用, (1)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" channelActive 被调用  ");
        context = ctx; //因为我们在其它方法会使用到 ctx
    }

    /**
     * 收到服务器的数据后会被调用，读取服务提供方返回的结果，并唤醒在call方法等待的线程 (4)
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(" channelRead 被调用  ");
        result = msg.toString();
        //此类中的call方法与该channelRead方法具有同步关系
        //rpc调用call方法需要同步等待该channelRead方法获取到服务提供方的结果，所以此处需要唤醒在call方法等待的线程
        this.notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 被代理对象调用, 发送数据给服务器 (3) -> (wait)等待被channelRead方法唤醒 -> 返回结果 (5)
     */
    @Override
    public synchronized Object call() throws Exception {
        System.out.println(" call 被调用，发消息给服务提供方");
        // 发消息给服务提供方(rpc调用的传参)
        context.writeAndFlush(param);
        //进行wait等待channelRead方法获取到服务器的结果后进行唤醒
        this.wait();
        System.out.println(" call 被调用，获取到服务器的结果后被唤醒");
        //服务方返回的结果
        return result;
    }

}
