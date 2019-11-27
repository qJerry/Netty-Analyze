package cn.binary.jerry.nio;

import cn.binary.jerry.nio.config.AppConfig;
import cn.binary.jerry.nio.server.HttpServer;
import cn.binary.jerry.nio.server.adapter.AbstractControllerAdapter;
import cn.binary.jerry.nio.server.api.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
public class ServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.refresh();

        new HttpServer(new AbstractControllerAdapter() {
            @Override
            public ApiController getControllerByUri(String controllerName) {
                logger.info("================= getControllerByUri {} =================", controllerName);
                ApiController controller = null;
                if(null != controllerName && !controllerName.isEmpty()) {
                    controller = context.getBean(controllerName, ApiController.class);
                }
                return controller;
            }
        }).start(8082);
    }
}
