package com.atguigu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 每个客户端连接会对应一个channel，一个channel对应一个buffer
 *
 * channel注册到selector上，并关注某些事件，
 * selector通过 {@link Selector#select()} 判断是否有事件发生，
 * 若有事件发生则通过 {@link Selector#selectedKeys()} 获取有事件发生的channel对应的selectionKeys，
 * 并轮询他们，通过selectionKey反向获取channel，若是连接事件（首次），则 {@link ServerSocketChannel#accept()} 接受客户端连接，
 * 并把accept()返回的socketChannel注册到selector，关注事件为 OP_READ，同时给该socketChannel关联一个Buffer，
 * 那么当客户端发送数据时会监听到读事件，则获取到客户端的channel对应的buffer进行读写操作
 *
 * {@link Selector#selectedKeys()}：获取【有事件发生的】channel对应的selectedKeys
 * {@link Selector#keys()}：获取【所有】注册到selector上的channel对应的selectedKeys
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {

        //创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到一个 Selector 对象
        Selector selector = Selector.open();

        //绑定一个端口6666, 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把 serverSocketChannel 注册到  selector 关注的事件为 OP_ACCEPT（连接）
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("注册后的selectionKey数量=" + selector.keys().size()); // 1

        //循环等待客户端连接
        while (true) {
            //这里我们等待1秒，如果没有事件发生则continue
            if (selector.select(1000) == 0) { //没有事件发生
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }

            //如果返回的>0, 就获取到相关的 selectionKey集合
            //1.如果返回的>0， 表示已经获取到关注的事件
            //2. selector.selectedKeys() 返回关注事件的集合
            //   通过 selectionKeys 反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys 数量 = " + selectionKeys.size());

            //遍历 Set<SelectionKey>, 使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key 对应的通道发生的事件做相应处理
                if (key.isAcceptable()) { //如果是 OP_ACCEPT, 有新的客户端连接
                    //该该客户端生成一个 SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    //将 SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到selector, 关注事件为 OP_READ， 同时给socketChannel关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接后 ，注册的selectionKey 数量=" + selector.keys().size()); //2,3,4..
                }
                if (key.isReadable()) {  //发生 OP_READ（读事件）
                    //通过key反向获取到对应channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("from 客户端 " + new String(buffer.array()));
                }

                //手动从集合中移动当前的selectionKey, 防止重复操作
                keyIterator.remove();
            }

        }

    }
}
