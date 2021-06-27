package cn.itcast.learn;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static cn.itcast.learn.ByteBufferUtil.debugRead;

@Slf4j
//nio来理解阻塞模式 单线程
public class Server {
    public static void main(String[] args) throws Exception{

        ByteBuffer buffer = ByteBuffer.allocate(16);

        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();


        ssc.configureBlocking(false);//切换非阻塞模式
        //2.绑定端口
        ssc.bind(new InetSocketAddress(8080));

        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true){
            SocketChannel sc = ssc.accept(); //阻塞方法
            if (sc!=null){
                log.debug("connected....{}",sc);
                sc.configureBlocking(false);
                channels.add(sc);
            }

            for (SocketChannel channel : channels) {
                int read = channel.read(buffer);//阻塞方法,线程停止运行
                if(read>0){
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read....{}",channel);
                }
            }
    }


    }
}
