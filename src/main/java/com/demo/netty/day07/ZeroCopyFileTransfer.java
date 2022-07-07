package com.demo.netty.day07;

import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;

/**
 * 通过FileRegion类，使用零拷贝技术，快速在两个Channel中传输文件
 */
public class ZeroCopyFileTransfer {
    //将文件File写入到targetChannel中
    public void transfer(File file, Channel targetChannel) throws Exception {
        FileInputStream in = new FileInputStream(file);
        //以该文件的完整长度，创建一个DefaultFileRegion对象
        FileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length());
        //将这个FileRegion写入到目标Channel中
        ChannelFuture future = targetChannel.writeAndFlush(region);
        future.addListener(
            new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        // 如果传输失败，这里要处理一下
                    }
                }
            }
        );
    }
}
