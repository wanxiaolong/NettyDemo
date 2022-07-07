package com.demo.netty.day07;

import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileInputStream;

/**
 * 使用ChunkedStream传输文件内容
 */
public class ChunkedWriteInitializer extends ChannelInitializer<Channel> {

    private final File file;
    private final SslContext sslContext;

    public ChunkedWriteInitializer(File file, SslContext sslContext) {
        this.file = file;
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //使用SSL传输
        pipeline.addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
        pipeline.addLast(new ChunkedWriteHandler());
        //一旦与Client建立连接，就开始向Client写文件数据
        pipeline.addLast(new WriteStreamHandler());
    }

    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            //ChunkedStream extends ChunkedInput
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }
    }
}
