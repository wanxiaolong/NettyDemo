package com.demo.netty.day07;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 基于行分隔的解码器
 */
public class LineBasedInitializer extends ChannelInitializer<Channel> {

    public static final int MAX_FRAME_LENGTH = 64 * 1024;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //按照行解码。行的最大长度为64k
        pipeline.addLast(new LineBasedFrameDecoder(MAX_FRAME_LENGTH));
        //按照指定的分隔符解码。
        //ByteBuf delimiter = Unpooled.copiedBuffer("SPLITTER".getBytes());
        //pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH, delimiter));
        pipeline.addLast(new FrameHandler());
    }


    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // do something with the data extracted from the frame
        }
    }
}
