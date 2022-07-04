package com.demo.netty.day05;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;

/**
 * 处理WebSocket中的接续帧
 */
public class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
        //处理接续帧
    }
}
