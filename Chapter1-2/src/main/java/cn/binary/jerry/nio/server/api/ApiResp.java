package cn.binary.jerry.nio.server.api;

import cn.binary.jerry.nio.common.GsonUtils;
import cn.binary.jerry.nio.server.adapter.AbstractControllerAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * <p>Title:Netty-Learing</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2019/11/18
 */
@Data
@Slf4j
public class ApiResp implements Serializable {

    private static final long serialVersionUID = -8826517176378050058L;

    private static final Charset charset = Charset.forName("UTF-8");;

    private int result = 0;

    private String msg;

    private String data;

    public ApiResp() {
    }

    public static ApiResp create(String data){
        ApiResp ret = new ApiResp();
        ret.setMsg("success");
        ret.setData(data);
        return ret;
    }
    public static ApiResp create(int result, String msg){
        ApiResp ret = new ApiResp();
        ret.setResult(result);
        ret.setMsg(msg);
        ret.setData(null);
        return ret;
    }
    public static ApiResp create(int result, String msg, String data){
        ApiResp ret = new ApiResp();
        ret.setResult(result);
        ret.setMsg(msg);
        ret.setData(data);
        return ret;
    }

    public static ApiResp success(HttpResponseStatus status){
        ApiResp ret = new ApiResp();
        return ret;
    }

    public static FullHttpResponse response(AbstractControllerAdapter adapter, ApiReq req) {
        ApiResp resp;
        ByteBuf resultBuf = null;
        HttpResponseStatus status = HttpResponseStatus.OK;
        try {
            ApiController controller = adapter.getControllerByUri(req.getControllerName());
            if(Objects.isNull(controller)) {
                status = HttpResponseStatus.NOT_FOUND;
            } else {
                resp = controller.invoke(req.getInvokeMethodName(), req);
                resultBuf = Unpooled.copiedBuffer(GsonUtils.toString(resp), CharsetUtil.UTF_8);
            }
        } catch (Exception e) {
            log.error("~~~~~~~~~~~~~~ controller error : {}", e.getMessage(), e);
            status = HttpResponseStatus.NOT_FOUND;
        }
        if(Objects.isNull(resultBuf)) {
            resultBuf = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, resultBuf);
        // 添加响应头信息
        HttpHeaders headers = response.headers();
        headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON  + "; charset=UTF-8");
        headers.add(HttpHeaderNames.CONTENT_LENGTH, resultBuf.readableBytes());

        return response;
    }
}
