package com.demo.netty.day09;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 这个Handler处理的消息类型是FullHttpRequest。
 * 主要用于在握手升级之前的数据传输
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //表示协议从HTTP/S升级到WebSocket时的触发url。当请求是这个url时，握手协议会升级
    private final String wsUri;
    //表示index.html页面的文件
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            String path = location.toURI() + "index.html";
            //确保path不包含file:前缀
            path = path.startsWith("file:") ? path.substring(5) : path;
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    //构造器指定要出发握手升级成WebSocket的URI
    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equals(request.uri())) {
            //如果请求协议升级，则增加消息的引用计数(调用retain方法)，并将它传递给下一个ChannelInboundHandler。
            //之所以需要调用retain()方法，是因为当某个ChannelInboundHandler的实现重写channelRead()方法时，
            //它将负责显式的释放收到的消息(即引用计数-1)。而本类的父类SimpleChannelInboundHandler实现了
            //ChannelInboundHandler接口，因此为了让后面的InboundHandler可以处理该消息，这里要增加引用计数。
            ctx.fireChannelRead(request.retain());
        } else {
            //如果client发送了HTTP1.1的头信息：Expect: 100-continue，那么HttpRequestHandler将发送一个
            //100 Continue的响应。在该HTTP头信息被设置后，HttpRequestHandler将会写回一个HttpResponse给client。
            //注意这个Response不是FullHttpResponse，因为它只是响应的第一个部分。
            //此外，这里也不会调用writeAndFlush()方法，只有在结束的时候才会调用。
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            //Response的协议和Request相同
            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            HttpHeaders headers = response.headers();
            headers.set(HttpHeaders.Names.CONTENT_TYPE, "text/html; chaset=UTF-8");
            boolean isKeepAlive = HttpHeaders.isKeepAlive(request);
            if (isKeepAlive) {
                headers.set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                //如果Request的Header中有keep-alive，则在Response中也添加Connection: keep-alive
                headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            //这里写给client的不是FullHttpResponse，这只是Response的第一个部分，并且这里也不会调用writeAndFlush()方法
            ctx.write(response);

            //通过检查pipeline中是否有SslHandler来判定是否需要对文件进行加密
            if (ctx.pipeline().get(SslHandler.class) == null) {
                //如果传输时不需要对文件进行加密，则可以以零拷贝的方式将文件写到client
                ctx.write(new DefaultFileRegion(file.getChannel(),0, file.length()));
            } else {
                //如果需要加密文件内容，则可以以大数据块的方式传输文件
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            //写一个LastHttpContent来标记响应结束，并冲刷到Client。
            //注意最后一次写入需要调用writeAndFlush()方法。
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            //如果client不需要keep-alive，则发送文件完成后关闭Channel
            if (!isKeepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    //向Channel中发送"100 continue"的HTTP消息
    private static void send100Continue(ChannelHandlerContext context) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        context.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
