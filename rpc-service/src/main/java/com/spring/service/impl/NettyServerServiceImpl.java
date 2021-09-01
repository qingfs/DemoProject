package com.spring.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Author xuyj
 * @Description
 **/
public class NettyServerServiceImpl extends Thread {
    private Integer port;
    public NettyServerServiceImpl(Integer port){
        this.port = port;
    }

    @Override
    public void run(){
        // 根据主机名和端口号创建ip套接字地址（ip地址+端口号）
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        // 主线程组，处理Accept连接事件的线程，这里线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 工作线程，处理hadnler的工作线程，其实也就是处理IO读写，线程数据默认为 CPU 核心数乘以2
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 创建ServerBootstrap实例
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)//初始化ServerBootstrap的线程组
                .channel(NioServerSocketChannel.class)// 设置将要被实例化的ServerChannel类
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 在有连接进入后进行ChannelHandler的编排

                        // 此处责任链添加处理器
                        nioSocketChannel.pipeline().addLast(new TCPServerHandler());
                    }

                })
                .localAddress(socketAddress)
                .option(ChannelOption.SO_BACKLOG, 1024)//设置队列大小
                .childOption(ChannelOption.SO_KEEPALIVE, true);// 是否启动心跳保活机制

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(socketAddress).sync();
            System.out.println("TCP服务开始监听端口：" + socketAddress.getPort());
            if (channelFuture.isSuccess()){
                System.out.println("TCP服务启动成功");
            }
            // 主线程执行到这里就 wait 子线程结束，子线程才是真正监听和接受请求的，
            // closeFuture()是开启了一个channel的监听器，负责监听channel是否关闭的状态，
            // 如果监听到channel关闭了，子线程才会释放，syncUninterruptibly()让主线程同步等待子线程结果
            channelFuture.channel().closeFuture().sync();
            System.out.println("TCP服务已关闭");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServerServiceImpl(6666).start();
    }

}
