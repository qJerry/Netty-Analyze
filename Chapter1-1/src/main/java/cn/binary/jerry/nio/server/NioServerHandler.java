package cn.binary.jerry.nio.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: A processor that can be used to parse the request parameters, to find a method and execute it.</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
public class NioServerHandler {

    /**
     * request
     */
    private NioReq req;

    public NioServerHandler(NioReq req) {
        this.req = req;
    }

    public NioResp handle() throws IOException {
        NioResp resp = new NioResp();
        resp.setProtocol("HTTP/1.1");
        resp.setCode(200);

        /**
         * 这里可以做业务逻辑的相关处理，比如通过反射找到对应的方法体，执行对应的业务逻辑
         */
        JsonObject object = new JsonObject();
        object.addProperty("a", 1);
        object.addProperty("b", 2);

        resp.getData().write(object.toString().getBytes());
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", object.toString().length());
        resp.setHeaders(headers);

        log.debug("################ response data: {} ################", object);
        return resp;
    }


}
