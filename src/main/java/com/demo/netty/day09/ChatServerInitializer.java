package com.demo.netty.day09;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        //只需向pipeline中安装一堆handler即可
        ChannelPipeline pipeline = ch.pipeline();
        //1.将字节解码为HttpRequest, HttpContent, LastHttpContent，并反过来将它们编码为字节
        pipeline.addLast(new HttpServerCodec());
        //2.异步传输大文件，既不消耗大量内存，更不会出现OOM
        pipeline.addLast(new ChunkedWriteHandler());
        //3.将HttpMessage和后续多个HttpContent聚合为一个FullHttpRequest或FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));//64KB
        //4.处理WebSocket升级握手、PingWebSocketFrame/PongWebSocketFrame/CloseWebSocketFrame帧
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //5.处理TextWebSocketFrame和握手完成事件
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }

}
