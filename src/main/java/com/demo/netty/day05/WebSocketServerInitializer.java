package com.demo.netty.day05;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * Netty实现的WebSocket
 */
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {

    private final SslContext context;

    public WebSocketServerInitializer(SslContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //启用WebSocket的安全传输
        pipeline.addFirst(new SslHandler(context.newEngine(ch.alloc())));
        pipeline.addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(65536),
                //如果请求的是/websocket，则处理该升级握手
                new WebSocketServerProtocolHandler("/websocket"),
                new TextFrameHandler(),
                new BinaryFrameHandler(),
                new ContinuationFrameHandler()
        );
    }
}
