package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Test;

import static org.junit.Assert.*;

public class FrameChunkDecoderTest {
    @Test
    public void testDecode() {
        ByteBuf buf = Unpooled.buffer();
        //将0~8写入ByteBuf中
        for (int i = 0; i < 9; i++) {
            buf.writeInt(i);
        }

        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        //向Channel中写入2B，成功
        assertTrue(channel.writeInbound(input.readBytes(2)));
        try {
            //写入4B，失败
            channel.writeInbound(input.readBytes(4));
        } catch (TooLongFrameException e) {
            System.err.println("TooLongFrameException occurred");
            //expected Exception
        }
        //写入3B，成功。现在一共写入9字节，从input写入完成
        assertTrue(channel.writeInbound(input.readBytes(3)));
        //标记channel写入完成
        assertTrue(channel.finish());

        //读取一帧
        ByteBuf read = (ByteBuf)channel.readInbound();
        //从原来的ByteBuf中读取2B，断言二者相等
        assertEquals(buf.readSlice(2),read);

        //再读一帧
        read = (ByteBuf)channel.readInbound();
        //从原来的ByteBuf中跳过4B，再读取3B，断言二者相等
        assertEquals(buf.skipBytes(4).readSlice(3), read);

        read.release();
        buf.release();
    }
}
