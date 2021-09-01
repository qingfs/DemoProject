package com.spring.service.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Channel> CLIENT_MAP = new HashMap<>();


    /**
     * 客户端连接标识
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端已连接：" + ctx.channel().localAddress().toString());
        // 获取当前客户端的唯一标识
        String uuid = ctx.channel().id().asLongText();
        System.out.println("当前连接的客户端id：" + uuid);
        // 将其对应的标识和channel存入到map中
        CLIENT_MAP.put(uuid, ctx.channel());
    }


    /**
     * 读取客户端发送的消息
     * @param ctx
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 使用netty提供的ByteBuf生成字节Buffer，里面维护一个字节数组，注意不是JDK自带的ByteBuffer
        ByteBuf byteBuf = (ByteBuf) msg;
        // 读取byteBuf
        // 业务处理 
        // 回消息给客户端
        
    }

    /**
     * 客户端断开连接时触发
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开前，CLIENT_MAP："+ CLIENT_MAP);
        //当客户端断开连接时，清除map缓存的客户端信息
        CLIENT_MAP.clear();
        System.out.println(ctx.channel().localAddress().toString() + " 通道不活跃！并且关闭。");
        System.out.println("断开后，CLIENT_MAP：" + CLIENT_MAP);
        // 关闭流
        ctx.close();
    }

    /**
     * 发生异常时触发
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常情况: " + cause.toString());
    }
    
    
    /**
     * channelRead方法执行完成后调用，发送消息给客户端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush = write + flush：将数据写入缓存，并刷新
        // 需要对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("收到消息，返回ok!".getBytes()));
    }
}