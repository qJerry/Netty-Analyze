package cn.binary.jerry.nio.common;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * <p>Title:Netty-Learing</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2019/11/18
 */
public class GsonUtils {

    private static Gson gson = new Gson();

    public static <T> T parse(String json, Class<T> classz) {
        return gson.fromJson(json, classz);
    }

    public static String toString(Object obj) {
        String json;
        try {
            json = gson.toJson(obj);
        } catch (Exception e) {
            json = "";
        }
        return json;
    }

}
