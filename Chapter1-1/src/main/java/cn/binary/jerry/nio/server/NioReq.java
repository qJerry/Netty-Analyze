package cn.binary.jerry.nio.server;

import cn.binary.jerry.nio.common.HttpMethod;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Common request</p>
 *
 * @author Jerry
 * @version 1.0
 */
@Slf4j
@Data
public class NioReq {

    /**
     * HTTP协议
     */
    private String protocol;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求链接
     */
    private String uri;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 请求数据
     */
    private Map<String, String> data;

    /**
     * parse buffer
     * @param buffer
     * @return
     */
    public static NioReq decode(CharBuffer buffer) {
        NioReq req = new NioReq();
        String bufferStr = buffer.toString();

        try {
            String[] bufferSplit = bufferStr.split("\r\n");
            if(bufferSplit.length == 0) {
                return req;
            }
            String[] httpSplit = bufferSplit[0].split("\\s+");
            req.setMethod(httpSplit[0]);
            req.setUri(httpSplit[1]);
            req.setProtocol(httpSplit[2]);

            Map<String, String> headers = Maps.newHashMap();
            req.setHeaders(headers);
            Arrays.stream(bufferSplit).forEach(header -> {
                String[] headerSplit = header.split(": ");
                if(headerSplit.length <= 1)
                    return;
                headers.put(headerSplit[0], headerSplit[1]);
            });

            Map<String, String> data = null;
            if(HttpMethod.GET.toString().equals(req.getMethod())) {
                List<NameValuePair> params = URLEncodedUtils.parse(req.getUri(), Charset.forName("UTF-8"));
                data = params.stream().collect(Collectors.toMap(k -> k.getName(), v -> v.getValue()));
            } else if(HttpMethod.POST.toString().equals(req.getMethod())) {
                String postData = bufferSplit[bufferSplit.length - 1];
                data = new Gson().fromJson(postData, new TypeToken<Map<String, String>>(){}.getType());
            }
            log.debug("################ request data: {} ################", data);
            req.setData(data);
        } catch (JsonSyntaxException e) {
            log.error("request data {} error: {} \n{}", bufferStr, e.getMessage(), e);
        }
        return req;
    }
}
