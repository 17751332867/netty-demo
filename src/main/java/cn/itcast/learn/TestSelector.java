package cn.itcast.learn;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;

import static cn.itcast.learn.ByteBufferUtil.debugAll;
import static cn.itcast.learn.ByteBufferUtil.debugRead;

@Slf4j
public class TestSelector {


    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
    public static void main(String[] args) throws Exception{

        //1.创建Selector,管理多个Channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //2.建立selecto 与channel 的联系
        SelectionKey sscKey = ssc.register(selector, 0, null);
        log.debug("*********************ssckey:{}",sscKey);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while (true){
            selector.select();
            //4.处理事件 selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //处理key时要从SelectedKeys集合中删除，否则会异常
                iterator.remove();
                log.debug("-------key:{}",key);
                //5.区分事件类型
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0,  buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}",sc);
                } else if (key.isReadable()){
                    try {
                        System.out.println("--------------------------------");
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);//正常断开，read的返回值是-1
                        if (read ==-1){
                            key.cancel();
                        }else {

                            split(buffer);
                            if (buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                             //buffer.flip();
                            // debugRead(buffer);
                           // buffer.clear();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();//客户端断开，将key从selector中取消
                    }
                }

            }
        }

    }
}
