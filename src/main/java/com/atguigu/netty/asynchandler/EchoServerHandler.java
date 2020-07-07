package com.atguigu.netty.asynchandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // group 就是充当业务线程池，可以将任务提交到该线程池
    // 这里我们创建了16个线程
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("EchoServer Handler 的线程是=" + Thread.currentThread().getName());

        //=================这种方法把耗时任务提交到任务队列中，但因为NioEventLoop本身继承了SingleThreadEventExecutor，所以这样提交的任务都是同一个线程排队处理的===================
//        ctx.channel().eventLoop().execute(() -> {
//            try {
                  //休眠5s，模拟耗时操作
//                Thread.sleep(5 * 1000);
//                //输出线程名
//                System.out.println("EchoServerHandler execute 线程是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
//            } catch (Exception ex) {
//                System.out.println("发生异常" + ex.getMessage());
//            }
//        });
//        ctx.channel().eventLoop().execute(() -> {
//            try {
                  //休眠5s，模拟耗时操作
//                Thread.sleep(5 * 1000);
//                //输出线程名
//                System.out.println("EchoServerHandler execute 线程2是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
//
//            } catch (Exception ex) {
//                System.out.println("发生异常" + ex.getMessage());
//            }
//        });

        //=================将任务提交到 group 线程池，异步执行=================
        group.submit(() -> {
            //接收客户端消息
            getMsg((ByteBuf) msg);
            //休眠10秒，模拟耗时操作
            Thread.sleep(10 * 1000);
            System.out.println("group.submit 的  call 线程是=" + Thread.currentThread().getName());
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
            return null;
        });

        group.submit(() -> {
            //接收客户端消息
            getMsg((ByteBuf) msg);
            //休眠10秒，模拟耗时操作
            Thread.sleep(10 * 1000);
            System.out.println("group.submit 的  call 线程是=" + Thread.currentThread().getName());
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
            return null;
        });

        group.submit(() -> {
            //接收客户端消息
            getMsg((ByteBuf) msg);
            //休眠10秒，模拟耗时操作
            Thread.sleep(10 * 1000);
            System.out.println("group.submit 的  call 线程是=" + Thread.currentThread().getName());
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
            return null;
        });


        //=================普通方式的阻塞等待方式=================
        //接收客户端消息
        getMsg((ByteBuf) msg);
        //休眠10秒，模拟耗时操作
        Thread.sleep(10 * 1000);
        System.out.println("普通调用方式的 线程是=" + Thread.currentThread().getName());
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));

        System.out.println("go on ");

    }


    /**
     * 接收客户端消息
     * @param msg 客户端发来的消息
     */
    private void getMsg(ByteBuf msg) {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
    }
}
