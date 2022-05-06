package com.demo.netty.day01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 标记这个Handler是可以被多个Channel共享。
 */
@ChannelHandler.Sharable
/**
 * 这个Handler用于从Client的Channel中读取数据，因此需要实现ChannelInboundHandler接口。
 * 它继承自ChannelInboundHandlerAdapter，而这个adapter对ChannelInboundHandler接口提供了默认实现。
 *
 * 因此我们只需要覆盖需要的方法即可。
 *
 * 问题：这里为什么没有继承自SimpleChannelInboundHandler类？
 * 答案：因为从channelRead()方法读取到的数据还要写回给Client，而且write()方法是异步的，就是可能出现
 *      channelRead()方法已经返回了，write()方法还没写完。而SimpleChannelInboundHandler会帮我们
 *      自动释放ByteBuf对象，因此这里不适合。
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    //对每个传入的消息都要调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将消息类型转换为ByteBuf
        ByteBuf buf = (ByteBuf)msg;
        //在Server端打印日志
        System.out.println("Server Received: " + buf.toString(CharsetUtil.UTF_8));
        //将消息写回给Client。
        //注意这里只有write，没有flush，因为这个channelRead()方法会被调用多次
        ctx.write(buf);
    }

    //读取消息完成时调用，即对channelRead()的调用是当前批量读取中的最后一条消息。
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //当Server端读取完成，读取的内容也已经写给Client了，此时需要flush
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                //这个CLOSE是一个Listener，它会在前序操作完成时关闭Channel。
                .addListener(ChannelFutureListener.CLOSE);
    }

    //读取操作期间，有异常的时候调用。
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
