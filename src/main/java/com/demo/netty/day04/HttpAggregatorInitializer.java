package com.demo.netty.day04;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

/**
 * 聚合HTTP碎片为完整的HTTP请求
 */
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpAggregatorInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            //Client端只需要添加HttpClientCodec
            //HttpClientCodec包含了RequestEncoder和ResponseDecoder
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            //Server端只需要添加HttpServerCodec
            //HttpServerCodec包含了RequestDecoder和ResponseEncoder
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }
}
