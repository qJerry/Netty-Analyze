package cn.binary.jerry.nio.common;

import com.google.common.base.Strings;

import java.util.Map;

/**
 * <p>Title:Netty-Learing</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
public class IPUtils {

    /**
     * 解析IP
     * @param headers
     * @return
     */
    public static String parseIp(Map<String, String> headers) {
        String ip = headers.get("X-Real-IP");

        if (verify(ip)) {
            ip = headers.get("x-forwarded-for");
        }
        if (verify(ip)) {
            ip = headers.get("Proxy-Client-IP");
        }
        if (verify(ip)) {
            ip = headers.get("WL-Proxy-Client-IP");
        }
        if(! Strings.isNullOrEmpty(ip) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    private static boolean verify(String ip) {
        return Strings.isNullOrEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }
}
