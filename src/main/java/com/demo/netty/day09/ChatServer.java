package com.demo.netty.day09;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * 聊天室的引导类
 */
public class ChatServer {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private Channel channel;
    protected static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
        final ChatServer server = new ChatServer();
        //用指定端口启动聊天服务器
        ChannelFuture future = server.start(new InetSocketAddress(PORT));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                server.destroy();
            }
        });
        future.addListener((f) -> {
            if (f.isSuccess()) {
                System.out.println("服务器启动成功，端口：" + PORT);
            }
        });
        //阻塞直到Future完成
        future.channel().closeFuture().syncUninterruptibly();
    }

    //启动服务器，并绑定指定端口
    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));
        ChannelFuture future = serverBootstrap.bind(address);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    //这里是为了子类SecureChatServer可以覆盖，所以是protected的
    protected ChannelInitializer createInitializer(ChannelGroup channelGroup) {
        return new ChatServerInitializer(channelGroup);
    }

    //关闭服务器
    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        eventLoopGroup.shutdownGracefully();
    }
}
