package com.demo.netty.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static String readFromStream(InputStream in) throws IOException {
        String result = "";
        if (in == null) {
            System.out.println("Stream is null, return an empty String.");
            return result;
        }
        byte[] buf = new byte[1024];
        int i;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = in.read(buf)) > 0) {
            out.write(buf,0, i);
        }
        byte[] data = out.toByteArray();
        return new String(data);
    }

    public static void main(String[] args) throws IOException {
        byte[] buf = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        System.out.println(readFromStream(in));
    }
}
