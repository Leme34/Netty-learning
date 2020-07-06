package com.atguigu.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * Channel-to-Channel传输
 *
 * FileChannel提供了transferTo()方法用来提高传输的效率
 * transferTo()允许将一个通道交叉连接到另一个通道，而不需要一个中间缓冲区来传递数据；
 * ps：这里不需要中间缓冲区有两层意思：1.不需要用户空间缓冲区来拷贝内核缓冲区，2.两个通道都有自己的内核缓冲区，两个内核缓冲区也可以做到无需拷贝数据；
 * 与从该通道读取并写入目标通道的简单循环相比，此方法的效率可能更高。
 * 许多操作系统可以将字节直接从文件系统高速缓存传输到目标通道，而无需实际复制它们。（零拷贝）
 */
public class NewIOClient {
    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 7001));
        String filename = "protoc-3.6.1-win32.zip";

        //得到一个文件channel
        FileChannel fileChannel = new FileInputStream(filename).getChannel();

        //准备发送
        long startTime = System.currentTimeMillis();

        //在linux下一个transferTo 方法就可以完成传输
        //在windows 下 一次调用 transferTo 只能发送8m , 就需要分段传输文件, 而且要主要
        //传输时的位置 =》 课后思考...
        //transferTo 底层使用到零拷贝
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("发送的总的字节数 =" + transferCount + " 耗时:" + (System.currentTimeMillis() - startTime));

        //关闭
        fileChannel.close();

    }
}
