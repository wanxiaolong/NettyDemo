package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 限制一个帧的大小为N字节。如果超过N字节，则丢弃该帧。
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //获取该帧的readableBytes
        int readableBytes = in.readableBytes();
        if (readableBytes > maxFrameSize) {
            //丢弃该帧，并抛出异常
            in.clear();
            throw new TooLongFrameException();
        }
        //从InboundChannel中读取数据
        ByteBuf buf = in.readBytes(readableBytes);
        //并写入到OutboundChannel中
        out.add(buf);

        //如果这里不是先从Inbound读取后再放入Outbound中，像下面这样：
        //out.add(in);
        //则会报错：io.netty.handler.codec.DecoderException:
        //FrameChunkDecoder.decode() did not read anything but decoded a message.
    }
}
