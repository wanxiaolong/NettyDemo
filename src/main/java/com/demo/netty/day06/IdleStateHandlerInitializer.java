package com.demo.netty.day06;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 模拟通过定时发送心跳来保持连接
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                //IdleStateHandler将在被触发时会发送一个IdleStateEvent事件，
                //pipeline中的HeartBeatHandler可以捕获这个事件，并发送一个心跳给对方，以保持连接
                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        //捕获这个事件，然后发送心跳以保持连接
        pipeline.addLast(new HeartBeatHandler());
    }
}
