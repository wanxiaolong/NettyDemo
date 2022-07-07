package com.demo.netty.day07;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 基于长度的解码器
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //表示帧的最大长度为64k，帧长度这个字段的偏移量为0，字段的长度为8
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                LineBasedInitializer.MAX_FRAME_LENGTH, 0, 8
        ));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // do something with the data extracted from the frame
        }
    }
}
