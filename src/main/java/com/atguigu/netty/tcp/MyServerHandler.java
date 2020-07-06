package com.atguigu.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 服务器一次调用channelRead0方法读取的字节数是不确定的，若客户端一次发送了n条消息过来，故可能存在以下四种情况：
 * （1）服务端分两次读取到了两个独立的数据包，分别是D1和D2，没有粘包和拆包
 * （2）服务端一次接受到了两个数据包，D1和D2粘合在一起，称之为【TCP粘包】
 * （3）服务端分两次读取到了数据包，第一次读取到了完整的D1包和D2包的部分内容，第二次读取到了D2包的剩余内容，这称之为【TCP拆包】
 * （4）服务端分两次读取到了数据包，第一次读取到了D1包的部分内容D1_1，第二次读取到了D1包的剩余部分内容D1_2和完整的D2包。
 * <p>
 * TCP粘包/拆包问题需要使用【编码器】来解决，见 {@link com.atguigu.netty.tcpprotocol}
 * 关键就是要解决 服务器端每次读取数据长度的问题，若这个问题解决，就不会出现服务器多读或少读数据的问题，从而避免的TCP 粘包、拆包。
 */
public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        //将buffer转成字符串
        String message = new String(buffer, StandardCharsets.UTF_8);

        System.out.println("服务器接收到数据 " + message);
        System.out.println("服务器接收到消息量=" + (++this.count));

        //服务器回送数据给客户端, 回送一个随机id ,
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString() + " ", StandardCharsets.UTF_8);
        ctx.writeAndFlush(responseByteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //cause.printStackTrace();
        ctx.close();
    }
}
