package com.demo.netty.day01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
/**
 * 这个Handler用于从Server的Channel中读取数据，因此需要实现ChannelInboundHandler接口。
 * 它继承自SimpleChannelInboundHandler，而这个类又继承自ChannelInboundHandlerAdapter，
 * 这个adapter对ChannelInboundHandler接口提供了默认实现。
 *
 * 除了一个必须实现的抽象方法channelRead0()外，其余的方法可以根据需要覆盖。
 * 这个channelRead0()方法是故意留给子类实现的，因为原始的channelRead()方法需要释放ByteBuf的引用
 * 这个步骤已经由SimpleChannelInboundHandler类帮我们自动完成了，因此我们只关心逻辑即可。
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    //当Client与Server的连接建立的时候调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //要发送的数据
        byte[] bytes = "Hello World!".getBytes(CharsetUtil.UTF_8);
        //当连接建立的时候，就给Server端发送一条消息，确保数据尽早写给Server
        ctx.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }

    //从Server收到消息时调用
    //数据可能分成多块发送，因此这个方法可能会被调用多次
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //从Channel中读取Server端发来的消息
        System.out.println("Client Received: " + msg.toString(CharsetUtil.UTF_8));
    }

    //处理过程中出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
