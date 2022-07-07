package com.demo.netty.day08;

import io.netty.channel.*;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

import java.io.Serializable;

/**
 * 使用JBoss Marshalling来序列化数据。
 */
public class MarshallingInitializer extends ChannelInitializer<Channel> {
    //编码器————序列化
    private final MarshallerProvider marshallerProvider;
    //解码器————反序列化
    private final UnmarshallerProvider unmarshallerProvider;

    public MarshallingInitializer(
            MarshallerProvider marshallerProvider,
            UnmarshallerProvider unmarshallerProvider) {
        this.marshallerProvider = marshallerProvider;
        this.unmarshallerProvider = unmarshallerProvider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //这里同时添加编码器和解码器，但是二者的方向是相反的，因此一个消息只会调用其中一个
        pipeline.addLast(new MarshallingDecoder(unmarshallerProvider));
        pipeline.addLast(new MarshallingEncoder(marshallerProvider));
        //MarshallingDecoder和ObjectHandler都是InboundHandler，
        //因此可以看做是数据先经过MarshallingDecoder解码后，再经过我们自己的Handler
        pipeline.addLast(new ObjectHandler());
    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            //自己的一些逻辑
        }
    }
}
