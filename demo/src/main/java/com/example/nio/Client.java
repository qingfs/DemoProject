package com.example.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
//        ManagerThread managerThread = new ManagerThread(selector, socketChannel);

        try {
            System.out.println("rely connection...");
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(Inet4Address.getLocalHost(),30889));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                // 判断是否连接成功
                if (key.isConnectable()) {
                    // 用finishConnect()判断连接的完成，这里轮询直到连接完成
                    while (!socketChannel.finishConnect()) {
                        System.out.println("Connecting...");
                    }
                    // 连接成功后，发送一个消息，并注册读事件，读取应答
                    doWrite((SocketChannel) key.channel());
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer readBB = ByteBuffer.allocate(1024);
                    int len=-1;
                    try {
                        len = channel.read(readBB);
                        if (len != 0) {
                            System.out.println("Service message:" + new String(readBB.array(),0,len, "UTF-8"));
                            readBB.clear();
                        } else if (len < 0){
                            key.cancel();
                            socketChannel.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                if (key.isWritable()) {
//                    SocketChannel channel = (SocketChannel) key.channel();
//                    // 注册一个该channel的读事件，用于接受返回信息
//                    channel.register(selector, SelectionKey.OP_READ);
//                    doWrite(channel);
//                }
            }
        }
    }

    /**
     * 发送一个指令
     * @param sc
     * @throws IOException
     */
    private static void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining())
            System.out.println("Send order 2 server succeed.");
    }

}
