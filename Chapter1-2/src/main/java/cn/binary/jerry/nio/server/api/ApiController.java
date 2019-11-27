package cn.binary.jerry.nio.server.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Interface base class for reflection to get subclass methods</p>
 *
 * @author Jerry
 * @version 1.0
 */
public class ApiController {

    public ApiResp invoke(String invokeMethodName, ApiReq req)  {
        try {
            Method method = getClass().getMethod(invokeMethodName, req.getClass());
            ApiResp result = (ApiResp) method.invoke(this, req);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
