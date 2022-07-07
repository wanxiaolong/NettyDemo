package com.demo.netty.day08;

import com.google.protobuf.MessageLite;
import io.netty.channel.*;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.Serializable;

/**
 * 使用Google Protobuf来序列化数据。
 */
public class ProtobufInitializer extends ChannelInitializer<Channel> {

    private final MessageLite messageLite;

    public ProtobufInitializer(MessageLite messageLite) {
        this.messageLite = messageLite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //这里同时添加编码器和解码器，但是二者的方向是相反的，因此一个消息只会调用其中一个
        pipeline.addLast(new ProtobufDecoder(messageLite));
        pipeline.addLast(new ProtobufEncoder());
        //ProtobufDecoder和ObjectHandler都是InboundHandler，
        //因此可以看做是数据先经过ProtobufDecoder解码后，再经过我们自己的Handler
        pipeline.addLast(new ObjectHandler());
    }

    //这个类暂时只处理Serializable类型的对象。
    //因此前面的ChannelHandler解码出来的对象是Serializable类型才能被这个Handler处理.
    public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            //自己的一些逻辑
        }
    }
}
