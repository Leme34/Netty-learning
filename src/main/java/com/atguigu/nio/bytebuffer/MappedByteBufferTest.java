package com.atguigu.nio.bytebuffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * java nio提供的FileChannel提供了map()方法（基于mmap+write方式：将文件直接映射到用户空间从而省掉原来内核read缓冲区copy数据到用户缓冲区，但是还是需要内核read缓冲区将数据copy到内核socket缓冲区），
 * 该方法可以在一个打开的文件和MappedByteBuffer之间建立一个虚拟内存映射，
 * MappedByteBuffer继承于ByteBuffer，类似于一个基于内存的缓冲区，
 * 只不过该对象的数据元素存储在磁盘的一个文件中；调用get()方法会从磁盘中获取数据，
 * 此数据反映该文件当前的内容，调用put()方法会更新磁盘上的文件，并且对文件做的修改对其他阅读者也是可见的；
 * <p>
 * MappedByteBuffer：实际类型 DirectByteBuffer，可让文件直接在内存(堆外内存)修改, 操作系统不需要拷贝一次
 * <p>
 * 对于大多数操作系统，将文件映射到内存要比通过通常的read和write方法读取或写入几十KB的数据更为昂贵。从性能的角度来看，通常仅需要将较大的文件映射到内存中。
 * 对于大文件：
 * 从代码层面上看，从硬盘上将文件读入内存，都要经过文件系统进行数据拷贝，并且数据拷贝操作是由文件系统和硬件驱动实现的，理论上来说，拷贝数据的效率是一样的。
 * 但是通过内存映射的方法访问硬盘上的文件，效率要比read和write系统调用高，这是为什么？
 * read()是系统调用，首先将文件从硬盘拷贝到内核空间的一个缓冲区，再将这些数据拷贝到用户空间，实际上进行了两次数据拷贝；
 * map()也是系统调用，但没有进行数据拷贝，真正的数据拷贝是在缺页中断处理时进行的（延迟加载），由于map()将文件直接映射到用户空间，所以中断处理函数根据这个映射关系，直接将文件从硬盘拷贝到用户空间，只进行了一次数据拷贝。
 * 所以，采用大文件采用内存映射的读写效率要比传统的read/write性能高。
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        //获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();

        /**当然请求的映射模式受到FileChannel对象的访问权限限制，
         * 如果在一个没有读权限的文件上启用READ_ONLY，将抛出NonReadableChannelException；
         * PRIVATE模式表示写时拷贝的映射，意味着通过put()方法所做的任何修改都会导致产生一个私有的数据拷贝并且该拷贝中的数据只有MappedByteBuffer实例可以看到；
         * 该过程不会对底层文件做任何修改，而且一旦缓冲区被施以垃圾收集动作（garbage collected），那些修改都会丢失；
         *
         * 参数1: 映射的模式，可选项包括：READ_ONLY，READ_WRITE，PRIVATE；
         * 参数2：从哪个位置开始映射，字节数的位置；（可以直接修改的起始位置）
         * 参数3：映射到内存的大小，即将 1.txt 从position开始向后的多少个字节映射到内存
         * 此处可以直接修改的范围就是 0-5
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');
        mappedByteBuffer.put(5, (byte) 'Y');//IndexOutOfBoundsException

        randomAccessFile.close();
        System.out.println("修改成功~~");


    }
}
