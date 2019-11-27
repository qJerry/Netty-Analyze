package cn.binary.jerry.nio.controller;

import cn.binary.jerry.nio.server.api.ApiController;
import cn.binary.jerry.nio.server.api.ApiReq;
import cn.binary.jerry.nio.server.api.ApiResp;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
public class UserController extends ApiController {

    public ApiResp get(ApiReq req) {
        return ApiResp.create("my name is " + req.getParam("name"));
    }

}
