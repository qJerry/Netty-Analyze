package cn.binary.jerry.nio.server;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.*;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Nio Server</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
public class NioServer {

    Selector selector;

    public NioServer(int port) {
        try {
            // open selector
            selector = Selector.open();
            // open socket channel
            ServerSocketChannel.open()
                // bind port
                .bind(new InetSocketAddress(port))
                // set blocking style，false:nio style，true:oio style
                .configureBlocking(false)
                // use selector to register this channel
                .register(selector, SelectionKey.OP_ACCEPT);
            log.debug("Server start, the port: {}", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listener() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(new NioServerListener(selector), 1000L, TimeUnit.MILLISECONDS);
    }
}
