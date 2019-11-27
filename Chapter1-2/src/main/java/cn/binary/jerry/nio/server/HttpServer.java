package cn.binary.jerry.nio.server;

import cn.binary.jerry.nio.server.adapter.AbstractControllerAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
public class HttpServer {

    private AbstractControllerAdapter controllerAdapter;

    public HttpServer(AbstractControllerAdapter controllerAdapter) {
        this.controllerAdapter = controllerAdapter;
    }

    static EventLoopGroup boss = new NioEventLoopGroup();
    static EventLoopGroup worker = new NioEventLoopGroup();

    public void start(int port) {
        log.info("############# start server at port: {}... #############", port);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                // 绑定两个组
                .group(boss, worker)
                // 创建NioServerSocketChannel实例
                .channel(NioServerSocketChannel.class)
                // 添加处理器Handler
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        // 为通道Channel进行初始化配置
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpRequestDecoder(),
                                new HttpResponseEncoder(),
                                new HttpServerHandler(controllerAdapter)
                        );
                    }
                })
                // 服务于boss线程(accept connect)
                // 设置TCP中的连接队列大小，如果队列满了，会发送一个ECONNREFUSED错误信息给C端，即“ Connection refused”
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 设置关闭tcp的Nagle算法（尽可能发送大块数据，避免网络中充斥着许多小数据块），要求高实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 设置启用心跳保活机制
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        log.info("############# shutdown server... #############");
    }
}
