package com.demo.netty.day02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty NIO实现。
 * EventLoopGroup：NioEventLoopGroup
 * channel的实现类：NioServerSocketChannel
 */
public class NettyNioServer {

    private static final ByteBuf DATA = Unpooled.copiedBuffer("Hi".getBytes());

    public static void main(String[] args) throws Exception {
        System.out.println("Server started, listening port=8888");
        new NettyNioServer().serve(8888);
    }

    public void serve(int port) throws Exception {
        //创建一个EventLoopGroup，这里使用的是NIO的实现类
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建一个ServerBootstrap用于引导Netty的Server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    //ServerBootstrap使用这个创建好的EventLoopGroup
                    .group(group)
                    //ServerBootstrap使用的Channel实现为NIO的SocketChannel
                    //这个实现要跟EventLoopGroup对应起来
                    .channel(NioServerSocketChannel.class)
                    //Server要绑定的端口号
                    .localAddress(port)
                    //为子Channel添加逻辑处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //要添加的ChannelHandler，通过继承这个Adapter类来实现
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                //在连接建立的时候，就向Client写入DATA，并关闭Channel
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(DATA.duplicate())
                                            //这个Listener会关闭Channel
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            //Server端完成绑定
            ChannelFuture future = serverBootstrap.bind().sync();
            //关闭Server
            future.channel().closeFuture().sync();
        } finally {
            //释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
