package cn.binary.jerry.nio.server;

import java.nio.ByteBuffer;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Custom attachment</p>
 *
 * @author Jerry
 * @version 1.0
 */
public class Buffers {

    ByteBuffer readBuffer;
    ByteBuffer writeBuffer;

    public Buffers(int read, int write) {
        this.readBuffer = ByteBuffer.allocate(read);
        this.writeBuffer = ByteBuffer.allocate(write);
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }
}
