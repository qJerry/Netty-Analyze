package cn.binary.jerry.nio.server.adapter;

import cn.binary.jerry.nio.server.api.ApiController;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: Controller adapter</p>
 *
 * @author Jerry
 * @version 1.0
 */
public abstract class AbstractControllerAdapter {

    public abstract ApiController getControllerByUri(String controllerName);
}
