package com.atguigu.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 在 ReplayingDecoder 不需要判断数据是否足够读取，内部会进行处理判断
 * 因此不需要像{@link MyByteToLongDecoder#decode}中用 {@code if (in.readableBytes() >= 8)} 去判断是否有足够字节数读取
 */
public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyByteToLongDecoder2 被调用");
        out.add(in.readLong());
    }
}
