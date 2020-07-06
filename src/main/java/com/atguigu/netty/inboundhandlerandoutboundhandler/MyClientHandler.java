package com.atguigu.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("服务器的ip=" + ctx.channel().remoteAddress());
        System.out.println("收到服务器消息=" + msg);
    }

    /**
     * 重写channelActive 发送数据
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler 发送数据");
        ctx.writeAndFlush(123456L); //发送的是一个long

        // ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd",CharsetUtil.UTF_8));
        /*
        分析为什么使用以上注释语句发送字符串到客户端不会被我们的 MyLongToByteEncoder 编码（处理）
            1. "abcdabcdabcdabcd" 是 16个字节
            2. 该处理器的前一个handler 是  MyLongToByteEncoder
            3. MyLongToByteEncoder 父类是 MessageToByteEncoder
            4. 其父类 MessageToByteEncoder 中的write方法如下：
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                ByteBuf buf = null;
                try {
                    //判断当前msg 是不是应该处理的类型，如果是就处理，不是就跳过encode，因为以上语句写出的是字符串而不是Long，所以不会被MyLongToByteEncoder处理
                    if (acceptOutboundMessage(msg)) {
                        @SuppressWarnings("unchecked")
                        I cast = (I) msg;
                        buf = allocateBuffer(ctx, cast, preferDirect);
                        try {
                            encode(ctx, cast, buf);
                        } finally {
                            ReferenceCountUtil.release(cast);
                        }

                        if (buf.isReadable()) {
                            ctx.write(buf, promise);
                        } else {
                            buf.release();
                            ctx.write(Unpooled.EMPTY_BUFFER, promise);
                        }
                        buf = null;
                    } else {
                        ctx.write(msg, promise);
                    }
                }
                ...
           }
           5. 因此我们编写 X2XEncoder 要注意传入的数据类型和处理的数据类型一致
        */

    }
}
