package com.demo.netty.day03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        //向buf中依次写入负数：-1, -2, ... , -9
        for (int i = 1; i < 10; i++) {
            buf.writeInt(-1 * i);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        //把buf写入channel中
        assertTrue(channel.writeOutbound(buf));
        //标记channel为完成状态
        assertTrue(channel.finish());

        //从channel中读取数据，并断言他们都是正数
        for (Integer i = 1; i < 10; i++) {
            //对于EmbeddedChannel，readOutbound()和writeOutbound()对应
            assertEquals(i, channel.readOutbound());
        }
        //channel没有数据可读
        assertNull(channel.readOutbound());
    }
}
