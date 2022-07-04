package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 取整数的绝对值。
 * 扩展MessageToMessageEncoder来把消息从一种格式编码为另一种格式。
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //一个int是4字节
        while(in.readableBytes() >= 4) {
            int oldValue = in.readInt();
            int newValue = Math.abs(oldValue);
            //将绝对值放进OutboundChannel中
            out.add(newValue);
        }
    }
}
