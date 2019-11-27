package cn.binary.jerry.nio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Service listener</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
public class NioServerListener implements Runnable {

    private static final Charset charset = Charset.forName("UTF-8");;

    Selector selector;

    public NioServerListener(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 获取已就绪的键数量
                int select = selector.select();
                if(select == 0)
                    continue;

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    try {
                        // 是否令牌失效
                        if(! key.isValid()) {
                            return;
                        }
                        // 是否已确认连接
                        if(key.isAcceptable()) {
                            accept(key);
                        }
                        // 是否可读
                        else if(key.isReadable()) {
                            read(key);
                        }
                        // 是否可写
                        else if(key.isWritable()) {
                            write(key);
                        }
                    } catch (IOException e) {
                        key.cancel();
                        key.channel().close();
                    }
                }

            }
        } catch (IOException e) {
            log.error("listener error: {}, \n{}", e.getMessage(), e);
        }
    }

    /**
     * 接收连接
     * @param key
     */
    private void accept(SelectionKey key) throws IOException {
        try {
            // 服务器套接字通道
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            // 接收后，开始注册读事件
            channel.register(selector, SelectionKey.OP_READ, new Buffers(1024, 1024));
            log.debug("================= remote client connecting: {} =================", channel.getRemoteAddress());
        } catch (IOException e) {
            log.error("accept error : {}, \n{}", e.getMessage(), e);
            throw e;
        }
    }

    private void read(SelectionKey key) throws IOException {
        Buffers buffers = (Buffers) key.attachment();
        ByteBuffer readBuffer = buffers.getReadBuffer();
        ByteBuffer writeBuffer = buffers.getWriteBuffer();

        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            int read = socketChannel.read(readBuffer);
            if(read <= 0)
                return;
            // 翻转缓冲区，limit->position, position->0
            readBuffer.flip();
            // 解码缓冲区的数据，position->limit, limit->limit
            CharBuffer buffer = charset.decode(readBuffer);
            // 倒带缓冲区，标记为被丢弃
            readBuffer.rewind();

            // 解析请求体
            NioReq request = NioReq.decode(buffer);
            // 逻辑处理，返回结果
            NioResp resp = new NioServerHandler(request).handle();
            response(writeBuffer, resp);
            // 清空缓冲区
            readBuffer.clear();
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        } catch (IOException e) {
            log.error("read error: {}, \n{}", e.getMessage(), e);
            throw e;
        }
    }

    private void write(SelectionKey key) throws IOException {
        try {
            // 检索获取buffer
            Buffers buffers = (Buffers) key.attachment();
            ByteBuffer writeBuffer = buffers.getWriteBuffer();
            // 翻转缓冲区，limit->position, position->0
            writeBuffer.flip();
            SocketChannel socketChannel = (SocketChannel) key.channel();
            int writeLen = 0;
            while (writeBuffer.hasRemaining()) {
                writeLen = socketChannel.write(writeBuffer);
                if(writeLen == 0) {
                    break;
                }
            }
            // 清除已读取的位置，未读数据被移到缓冲区的起始位置position，且新写入的数据写到未读数据后面
            writeBuffer.compact();
            if(writeLen != 0) {
                key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
            }
            charset.decode(writeBuffer);
        } catch (IOException e) {
            log.error("write error: {}, \n{}", e.getMessage(), e);
            throw e;
        }
    }

    private void response(ByteBuffer writeBuffer, NioResp resp) {
        StringBuilder build = new StringBuilder();
        build.append(resp.getProtocol()).append(" ").append(resp.getCode()).append("\r\n");
        resp.getHeaders().forEach((k, v) -> {
            build.append(k).append(": ").append(v).append("\r\n");
        });
        build.append("\r\n");
        writeBuffer.put(build.toString().getBytes());
        writeBuffer.put(resp.getData().toByteArray());
    }
}
