package com.demo.netty.day02;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java OIO实现。
 * 这段代码为每一个连接分配一个线程来处理，但只能处理中等数量的并发Client，
 * 并不能很好的伸缩到支撑成千上万的并发连接。
 */
public class PlainOioServer {

    private static final byte[] DATA = "Hi".getBytes();

    public static void main(String[] args) {
        System.out.println("Server started, listening port=8888");
        new PlainOioServer().serve(8888);
    }

    public void serve(int port) {
        try {
            //创建一个ServerSocket并监听指定端口
            final ServerSocket serverSocket = new ServerSocket(port);
            for (;;) {
                //接受Client连接，此方法将阻塞，直到有连接
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                //新起一个线程来处理Client请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            //写入数据给Client
                            out = clientSocket.getOutputStream();
                            out.write(DATA);
                            out.flush();
                        } catch (IOException e) {
                            System.err.println("Failed to process client socket " + clientSocket);
                            e.printStackTrace();
                        } finally {
                            //最后关闭连接
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                System.err.println("Failed to close client socket " + clientSocket);
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
