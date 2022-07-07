package com.demo.netty.day09;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 处理聊天消息的Handler。
 * 在协议升级成WebSocket后，数据是以帧的方式传输，因此需要一个文本类型的帧的处理器。
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * ChannelGroup是一个线程安全的Channel集合，它提供了一系列基于Channel的批量操作，
     * 使用它可以吧许多Channel分成有意义的组(比如按照service或者按照Channel状态来分)。
     * 一个close的channel会自动删除，一个channel可以属于不同的ChannelGroup。
     *
     * 这里为了实现"一个人进入聊天室，其余所有人都可以收到通知"这个需求，使用了ChannelGroup
     */
    private ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    //重写userEventTriggered方法以处理自定义事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //如果握手协议升级完成，则可以从pipeline中移除HttpRequestHandler这个处理器了，
            //因为不会接收任何HTTP消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            //通知所有已经连接的Client，有一个新的Client连上了。
            //group.writeAndFlush表示把消息写入group中每个channel，并且flush。
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            //将新的channel加到group中，便于可以接收新消息
            group.add(ctx.channel());
        } else {
            //其余的事件，本类不处理，交给父类处理(会传递给下一个ChannelInboundHandler)
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //如果从当前channel读到消息，则广播给所有的channel，实现了聊天消息共享
        //和HttpRequestHandler一样，对于retain()方法的调用是必须的，因为当channelRead0()方法返回时，
        //消息TextWebSocketFrame的引用次数会减少。由于操作都是异步的，因此writeAndFlush()方法可能会在
        //channelRead0()方法返回之后完成，所以为了保证消息一定可用，必须要调用retain()方法增加它的引用计数
        group.writeAndFlush(msg.retain());
    }
}
