package com.example.client;

import com.alibaba.fastjson.JSON;
import com.example.data.MessageFuture;
import com.example.data.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class NettyClientConnect {

    private final Map<Long,MessageFuture> futureMap = new ConcurrentHashMap<>();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void connect(String requestJson,Long threadId){
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().
                        // String解码器
                        addLast(new StringDecoder()).
                        addLast(new SimpleChannelInboundHandler<String>() {
                            // 服务端返回参数调用此方法
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                Response response = JSON.parseObject(msg, Response.class);
                                MessageFuture messageFuture = futureMap.get(threadId);
                                messageFuture.setMessage(response);
                            }

                            /**
                             * 连接上服务端调用此方法，发送请求
                             * @param ctx
                             * @throws Exception
                             */
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                futureMap.put(threadId,new MessageFuture());
                                countDownLatch.countDown();
                                ctx.writeAndFlush(Unpooled.copiedBuffer(requestJson, CharsetUtil.UTF_8));
                            }
                        });
            }
        }).connect("127.0.0.1", 20006);
    }

    /**
     * netty异步发送请求，在此处等待结果返回
     * @param threadId
     * @return
     */
    public Response getResponse(Long threadId){
        MessageFuture messageFuture = null;
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageFuture = futureMap.get(threadId);
        return messageFuture.getMessage();
    }
}