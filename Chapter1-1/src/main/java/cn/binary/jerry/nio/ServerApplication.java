package cn.binary.jerry.nio;

import cn.binary.jerry.nio.server.NioServer;

import java.io.IOException;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
public class ServerApplication {

    public static void main(String[] args) throws IOException {
        new NioServer(8083).listener();
    }
}
