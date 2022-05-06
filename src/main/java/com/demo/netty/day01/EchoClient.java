package com.demo.netty.day01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: EchoClient <host> <port>");
            return;
        }
        //从参数中解析host和port
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        //调用start()方法启动客户端
        new EchoClient(host, port).start();
    }

    public void start() throws Exception {
        //自定义的ChannelHandler对象
        //final EchoClientHandler handler = ;
        //使用非阻塞的NioEventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    //指定要使用的Channel实现。
                    //注意这里不是NioServerSocketChannel
                    .channel(NioSocketChannel.class)
                    //指定要连接的Server的host和port
                    .remoteAddress(host, port)
                    //指定要添加的ChannelHandler
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //在pipeline中安装自定义的handler
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //连接到Server。注意，在Client端，这里是调用connect()去连接Server
            ChannelFuture future = bootstrap.connect().sync();
            System.out.println("Server connected. address=" + host + ":" + port);
            //阻塞直到关闭Channel
            future.channel().closeFuture().sync();
        } finally {
            //关闭EventLoopGroup，释放所有的资源
            group.shutdownGracefully();
        }
    }
}
