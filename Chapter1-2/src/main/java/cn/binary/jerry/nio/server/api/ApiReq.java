package cn.binary.jerry.nio.server.api;

import cn.binary.jerry.nio.common.IPUtils;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: common request</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
@Data
public class ApiReq {

    /**
     * HTTP protocol
     */
    private String protocol;

    /**
     * ip
     */
    private String remoteIp;

    /**
     * request type
     */
    private String method;

    /**
     * request uri
     */
    private String uri;

    /**
     * request headers
     */
    private Map<String, String> headers;

    /**
     * request data
     */
    private Map<String, String> data = Maps.newHashMap();

    private String controllerName;

    private String invokeMethodName;

    public String getParam(String key) {
        return data.get(key);
    }

    public static ApiReq decode(HttpRequest request) {
        ApiReq req = new ApiReq();

        String uri = request.uri();
        req.setUri(uri);

        HttpMethod method = request.method();
        req.setMethod(method.name());

        Map<String, String> headers = Maps.newHashMap();
        request.headers().entries().stream().forEach(header -> {
            headers.put(header.getKey(), header.getValue());
        });
        req.setRemoteIp(IPUtils.parseIp(headers));

        int index = uri.indexOf("?");
        if(index > 0) {
            uri = uri.substring(0, index);
        }
        index = uri.lastIndexOf("/");
        if(0 == index) {
            req.setControllerName(uri.substring(1));
        } else if(index > 0 && (index + 1) < uri.length()) {
            req.setControllerName(uri.substring(1, index));
            req.setInvokeMethodName(uri.substring(index + 1));
        }
        return req;
    }

    /**
     * data parse
     * @param buffer
     * @return
     */
    public Map<String, String> parseData(ByteBuf buffer) {
        Map<String, String> data = Maps.newHashMap();

//        ByteBuf buffer = content.content();
        byte[] byteArr = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), byteArr);
        String body = new String(byteArr);

        if(HttpMethod.POST.name().equals(method)) {
            Map<String, String> map = new Gson().fromJson(body, new TypeToken<Map<String, String>>(){}.getType());
            map.forEach((k, v) -> data.put(k, v));
//            // get decoder param.
//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
//            List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
//            bodyHttpDatas.stream().forEach(httpData -> {
//                if(httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//                    MemoryAttribute attribute = (MemoryAttribute) httpData;
//                    data.put(attribute.getName(), attribute.getValue());
//                }
//            });
        }
        else if(HttpMethod.GET.name().equals(method)) {
            QueryStringDecoder decoder = new QueryStringDecoder(uri);
            decoder.parameters().entrySet().stream().forEach(param-> data.put(param.getKey(), param.getValue().get(0)));
        }
        return data;
    }



}
