package com.demo.netty.day02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Java NIO实现。
 * 功能和PlainOio完全一样，但是代码却大相径庭。从OIO到NIO的重构，相当麻烦。
 */
public class PlainNioServer {

    private static final ByteBuffer DATA = ByteBuffer.wrap("Hi".getBytes());

    public static void main(String[] args) throws IOException {
        System.out.println("Server started, listening port=8888");
        new PlainNioServer().serve(8888);
    }

    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        //设置该Channel为非阻塞的
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        //将Server绑定到指定端口
        serverSocket.bind(new InetSocketAddress(port));

        //打开Selector来处理Channel
        Selector selector = Selector.open();
        //把serverChannel注册到selector上，监听建立连接的事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        for (;;) {
            try {
                //selector开始等待事件，当有事件时，本方法会返回
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    //如果是连接建立的事件，那肯定是ServerSocketChannel，因为只有一个地方在监听ACCEPT事件
                    if (key.isAcceptable()) {
                        //直接强制类型转换即可
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        //接收Client连接
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);

                        //把clientChannel注册到selector上，监听写事件。
                        //这里还将DATA的副本对象以attachment的形式附加到SelectionKey上
                        client.register(selector, SelectionKey.OP_WRITE, DATA.duplicate());
                        System.out.println("Accepted connection from " + client);
                    }

                    //如果是可写事件，那肯定是SocketChannel，因为只有一个地方在监听WRITE事件
                    if (key.isWritable()) {
                        //直接强制类型转换即可
                        SocketChannel client = (SocketChannel) key.channel();
                        //从SelectionKey中取出attachment，就是之前我们放入的DATA对象
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while(buffer.hasRemaining()) {
                            //注意这里client.write()方法是向Client写数据，而不是由Client写给Server
                            if (client.write(buffer) == 0) {
                                //如果buffer写完，退出while循环
                                break;
                            }
                        }
                        //数据写完，就关闭Client
                        client.close();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e2) {
                        //ignore on close
                    }
                }
            }
        }
    }
}
