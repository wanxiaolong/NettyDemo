package com.demo.netty.day02;


import com.demo.netty.util.IOUtils;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8888);
        String response = IOUtils.readFromStream(socket.getInputStream());
        System.out.println("Client received: " + response);
    }
}
