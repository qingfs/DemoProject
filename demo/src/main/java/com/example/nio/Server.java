package com.example.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    public static void main(String[] args) throws IOException {
        // 1）打开ServerSocketChannel，用于监听客户端的连接
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 2）绑定监听端口，设置连接为非阻塞模式
        channel.bind(new InetSocketAddress(Inet4Address.getLocalHost(),30889));
        channel.configureBlocking(false);
        // 3）创建多路复用器
        Selector selector = Selector.open();
        // 4）将ServerSocketChannel注册到Selector上，监听ACCEPT事件
        channel.register(selector, SelectionKey.OP_ACCEPT);
        // 5）无限循环，轮询准备就绪的Key
        while (true){
            selector.select();//（1）
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey sKey = iterator.next();
                iterator.remove();
                // 接入事件
                if(sKey.isAcceptable()){
                    // 6）监听到新的客户端接入，建立物理链路
                    SocketChannel socketChannel = channel.accept();
                    // 7）设置客户端链路为非阻塞
                    socketChannel.configureBlocking(false);
                    // 8）将新接入的客户端连接注册到多路复用器上，监听读操作，读取客户端发送的网络信息
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    // 9）回写，通知成功
                    socketChannel.write(ByteBuffer.wrap("Connection Success!".getBytes("UTF-8")));
                    System.out.println("New connection success.");
                }
                if(sKey.isReadable()){
                    SocketChannel rChannel = (SocketChannel)sKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 4);
                    int len=-1;
                    try{
                        len = rChannel.read(byteBuffer);
                        if(len != 0){
                            byteBuffer.flip();
                            String message = new String(byteBuffer.array(),0, len, "UTF-8");
                            System.out.println("Client message:"+message);
                            byteBuffer.clear();
                            String currentTime = "QUERY TIME ORDER"
                                    .equalsIgnoreCase(message) ? new java.util.Date(
                                    System.currentTimeMillis()).toString()
                                    : "BAD ORDER";
                            // 根据指令获取当前时间并返回
                            rChannel.write(ByteBuffer.wrap((currentTime).getBytes("UTF-8")));
                        } else if (len < 0){
                            // 对端链路关闭
                            sKey.cancel();
                            rChannel.close();
                        }
                    }catch (IOException e){
                        sKey.cancel();
                        rChannel.close();
                        System.out.println("Client close connection.");
                    }
                }
            }
        }
    }
}
