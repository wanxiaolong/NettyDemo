package com.demo.netty.day09;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

/**
 * 使用SSL的聊天室
 */
public class SecureChatServer extends ChatServer {

    private final SslContext sslContext;

    public SecureChatServer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected ChannelInitializer createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, sslContext);
    }

    public static void main(String[] args) throws Exception {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContext.newServerContext(cert.certificate(), cert.privateKey());
        final SecureChatServer server = new SecureChatServer(context);

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
}
