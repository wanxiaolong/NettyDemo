package com.demo.netty.day09;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * 使用SSL的ChannelInitializer
 */
public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SslContext sslContext;

    public SecureChatServerInitializer(ChannelGroup channelGroup, SslContext sslContext) {
        super(channelGroup);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);

        SSLEngine engine = sslContext.newEngine(ch.alloc());
        engine.setUseClientMode(false);

        //将SslHandler添加到pipeline的头部，作为第一个handler
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}
