package com.demo.netty.day06;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 处理连接闲置的Handler
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf BUF = Unpooled.copiedBuffer("HeartBeat", CharsetUtil.UTF_8);
    private static final ByteBuf HEART_BEAT_SEQUENCE = Unpooled.unreleasableBuffer(BUF);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //连接闲置事件，需要发送心跳，不关闭连接
            ctx.writeAndFlush(HEART_BEAT_SEQUENCE.duplicate())
                    //仅仅在发送心跳失败的时候关闭连接
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            //其他事件，不处理，调用super将事件传递给下一个ChannelHandler
            super.userEventTriggered(ctx, evt);
        }
    }
}
