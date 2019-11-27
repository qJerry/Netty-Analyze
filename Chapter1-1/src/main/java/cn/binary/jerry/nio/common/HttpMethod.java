package cn.binary.jerry.nio.common;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    String method;

    HttpMethod(String method) {
        this.method = method;
    }

}
