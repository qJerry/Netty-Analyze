package cn.binary.jerry.nio.server;

import cn.binary.jerry.nio.server.adapter.AbstractControllerAdapter;
import cn.binary.jerry.nio.server.api.ApiReq;
import cn.binary.jerry.nio.server.api.ApiResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private AbstractControllerAdapter adapter;

    private ApiReq req;

    private static final String favicon = "favicon.ico";

    private ByteBuf byteBuf;

    public HttpServerHandler(AbstractControllerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            if(msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;

                req = ApiReq.decode(request);
                if(favicon.equalsIgnoreCase(req.getControllerName())) {
                    return;
                }
            } else if(msg instanceof LastHttpContent) {
                String controllerName = req.getControllerName();
                if(favicon.equalsIgnoreCase(controllerName)) {
                    return;
                }
                HttpContent content = (HttpContent) msg;
                byteBuf = content.content();
                Map<String, String> data = req.parseData(byteBuf);
                req.setData(data);
                log.debug("================= request data: =================\n{} ",
                        data.entrySet().stream().map(entry -> new StringBuilder().append(entry.getKey()).append(" : ").append(entry.getValue()).toString()).collect(Collectors.joining("\n")));

                FullHttpResponse response = ApiResp.response(adapter, req);

                ctx.write(response);
                log.debug("================= request end, cost {} ms =================", System.currentTimeMillis() - startTime);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        try {
            ctx.channel().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
