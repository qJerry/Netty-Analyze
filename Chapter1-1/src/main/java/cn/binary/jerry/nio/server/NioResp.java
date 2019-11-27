package cn.binary.jerry.nio.server;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Common response</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Data
public class NioResp {

    /**
     * 协议
     */
    private String protocol = "HTTP/1.1";
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 状态码内容
     */
    private String msg;
    /**
     * 返回头
     */
    private Map<String, Object> headers;
    /**
     * 返回数据
     */
    private ByteArrayOutputStream data = new ByteArrayOutputStream();




}
