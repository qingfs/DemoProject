package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author xuyongjia
 * @date 2020/7/27
 */
public class NettyService {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap
                    // 指定eventLoopGroup
                    .group(parentGroup, childGroup)
                    // 指定使用NIO进行通信
                    .channel(NioServerSocketChannel.class)
                    // 指定处理器，用于处理childGroup中的eventLoop所绑定的线程
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 初始化channel
                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            // 按顺序往ChannelPipeline中添加处理类
                            // 解码
                            pipeline.addLast(new HttpServerCodec());
                            // 自定义业务处理
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("ddddddddddddddddddddddd");
                                }
                            });
                        }
                    });
            // 指定当前服务器所监听的端口号
            // bind()方法的执行是异步的
            // sync()方法会使bind()操作与后续的代码的执行由异步变为了同步
            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("服务器启动成功。监听的端口号为：8888");

            // closeFuture()的执行是异步的
            // 在此处等待，直到Channel调用了close()方法并关闭成功后才会触发closeFuture()方法的执行
            future.channel().closeFuture().sync();
        } finally {
            // 优化的关闭
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("-------------- " + ctx.channel());


        System.out.println("msg = " + msg.getClass());
        System.out.println("客户端地址 = " + ctx.channel().remoteAddress());

        if(msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            System.out.println("请求方式：" + request.method().name());
            System.out.println("请求URI：" + request.uri());

            if("/favicon.ico".equals(request.uri())) {
                System.out.println("不处理/favicon.ico请求");
                return;
            }

            // 构造response的响应体
            ByteBuf body = Unpooled.copiedBuffer("hello netty world", CharsetUtil.UTF_8);
            // 生成响应对象
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
            // 获取到response的头部后进行初始化
            HttpHeaders headers = response.headers();
            headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            headers.set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());

            // 将响应对象写入到Channel
            // ctx.write(response);
            // ctx.flush();
            // ctx.writeAndFlush(response);
            // ctx.channel().close();
            ctx.writeAndFlush(response)
                    // 添加channel关闭监听器
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    static class SomeServerHandler extends ChannelInboundHandlerAdapter {
        /**
         *  当Channel中有来自于客户端的数据时就会触发该方法的执行
         * @param ctx  上下文对象
         * @param msg   就是来自于客户端的数据
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            System.out.println("-------------- " + ctx.channel());


            System.out.println("msg = " + msg.getClass());
            System.out.println("客户端地址 = " + ctx.channel().remoteAddress());

            if(msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                System.out.println("请求方式：" + request.method().name());
                System.out.println("请求URI：" + request.uri());

                if("/favicon.ico".equals(request.uri())) {
                    System.out.println("不处理/favicon.ico请求");
                    return;
                }

                // 构造response的响应体
                ByteBuf body = Unpooled.copiedBuffer("hello netty world", CharsetUtil.UTF_8);
                // 生成响应对象
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
                // 获取到response的头部后进行初始化
                HttpHeaders headers = response.headers();
                headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                headers.set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());

                // 将响应对象写入到Channel
                // ctx.write(response);
                // ctx.flush();
                // ctx.writeAndFlush(response);
                // ctx.channel().close();
                ctx.writeAndFlush(response)
                        // 添加channel关闭监听器
                        .addListener(ChannelFutureListener.CLOSE);
            }
        }

        /**
         *  当Channel中的数据在处理过程中出现异常时会触发该方法的执行
         * @param ctx  上下文
         * @param cause  发生的异常对象
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            // 关闭Channel
            ctx.close();
        }
    }
}

