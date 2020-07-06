package com.atguigu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {
        //创建一个ByteBuf
        //1. 创建 对象，该对象包含一个数组arr , 是一个byte[10]
        //2. 在netty的buffer中，不需要使用 flip 进行反转，其底层维护了 readerIndex 和 writerIndex
        //3. 通过 readerIndex 和  writerIndex 和 capacity，将buffer分成三个区域
        // 0---readerIndex：已经读取的区域
        // readerIndex---writerIndex：可读的区域
        // writerIndex -- capacity：可写的区域
        ByteBuf buffer = Unpooled.buffer(10);

        for(int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("capacity=" + buffer.capacity());//10
        // 指定索引下标读取ByteBuf输出
//        for(int i = 0; i<buffer.capacity(); i++) {
//            System.out.println(buffer.getByte(i));
//        }
        // 使用ByteBuf内置的readerIndex去读取
        for(int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.readByte());
        }
        System.out.println("执行完毕");
    }
}
