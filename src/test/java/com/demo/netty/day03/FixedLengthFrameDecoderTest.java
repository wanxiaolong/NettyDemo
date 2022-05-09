package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded() {
        //分配一个ByteBuf用于保存输入数据
        ByteBuf buf = Unpooled.buffer();
        //填充这个ByteBuf的初始值，
        for (int i = 0; i < 9; i++) {
            buf.writeByte((byte)i);
        }
        //获取它的一个视图
        ByteBuf input = buf.duplicate();
        //创建一个以FixedLengthFrameDecoder作为ChannelHandler的EmbeddedChannel
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));

        //将input写入EmbeddedChannel，这里如果返回成功，
        //则说明有消息到达了EmbeddeChannel的尾部
        //这里一次性写入了9个字节
        assertTrue(channel.writeInbound(input.retain()));
        //标记Channel为已完成状态
        assertTrue(channel.finish());

        //------下面从EmbeddedChannel中读取数据------

        //从Channel中读取一帧
        ByteBuf read = channel.readInbound();//返回：012
        //期望读取到的帧和从原来的buf中读取到的定长帧一样
        //这里readSlice(3)表示从ByteBuf的readerIndex中读取3个字节，
        //读取完成后，增加readerIndex
        assertEquals(buf.readSlice(3), read);
        read.release();

        //从Channel中再读一帧，从原来的buf中再读3个字节
        read = channel.readInbound();//返回：345
        assertEquals(buf.readSlice(3), read);
        read.release();

        //从Channel中再读一帧，从原来的buf中再读3个字节
        read = channel.readInbound();//返回：678
        assertEquals(buf.readSlice(3), read);
        read.release();

        //此时Channel中已经没有数据可读，因此readInbound将返回null
        assertNull(channel.readInbound());//返回：null
        //最后把不用的buf释放掉
        buf.release();
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte((byte)i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3));

        //先向EmbeddedChannel中写入2字节，由于FixedLengthFrameDecoder要3个字节
        //才能输出一个Frame，因此Channel尾部没有可读的数据，所以这里返回false
        assertFalse(channel.writeInbound(input.readBytes(2)));
        //向EmbeddedChannel中写入剩下的7字节，此时可以构造3个Frame，因此这里会返回true
        assertTrue(channel.writeInbound(input.readBytes(7)));
        //标记Channel为已完成状态
        assertTrue(channel.finish());

        //------下面从EmbeddedChannel中读取数据------

        ByteBuf read = channel.readInbound();//返回：012
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();//返回：345
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();//返回：678
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());//返回：null
        buf.release();
    }
}
