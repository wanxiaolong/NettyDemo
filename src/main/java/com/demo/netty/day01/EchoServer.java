package com.demo.netty.day01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: EchoServer <port>");
            return;
        }
        //解析参数中的端口号
        int port = Integer.parseInt(args[0]);
        //调用start()方法启动服务器
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler handler = new EchoServerHandler();
        //使用非阻塞的EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建一个ServerBootstrap用于引导一个服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    //指定要使用的Channel的实现。
                    //注意这里不是NioSocketChannel。
                    //这个实现类需要和EventLoopGroup对应起来。
                    .channel(NioServerSocketChannel.class)
                    //使用指定的端口设置Socket
                    .localAddress(port)
                    //childHandler运用于Server端接受的每个子连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //当一个新的连接被接受时，一个子Channel将被创建，initChannel()会被调用
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //在子channel的pipeline中安装自定义的handler。
                            //由于handler是@Shareable的，因此可以被多个子channel安全的共享
                            ch.pipeline().addLast(handler);
                        }
                    });
            //异步绑定服务器，对sync()的调用将导致当前线程阻塞，直到操作完成
            //注意，在Server端是调用bind()方法完成端口绑定
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("Server started. port=" + port);
            //closeFuture()方法会在关闭channel的时候收到通知。
            //这里，程序将会阻塞，直到channel关闭
            future.channel().closeFuture().sync();
        } finally {
            //关闭EventLoopGroup，释放所有的资源，并且关闭所有当前正在使用的Channel
            group.shutdownGracefully();
        }
    }
}
