package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 产生固定大小的Frame
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int FRAME_LENGTH;

    public FixedLengthFrameDecoder(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException(
                    "Frame length should be a positive number");
        }
        FRAME_LENGTH = length;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf in, List<Object> out) throws Exception {
        //只有在输入帧(ByteBuf)的内容足够时才解码
        while(in.readableBytes() >= FRAME_LENGTH) {
            //从输入的帧(ByteBuf)里读取指定长度的字节数，组成一个新的帧(ByteBuf)
            ByteBuf buf = in.readBytes(FRAME_LENGTH);
            //将解码后的帧添加到输出对象
            out.add(buf);
        }
    }
}
