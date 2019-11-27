package cn.binary.jerry.nio.config;

import cn.binary.jerry.nio.controller.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>Title:Netty-Learning</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 */
@ComponentScan("cn.binary.jerry.nio.controller")
public class AppConfig {

    @Bean("user")
    public UserController user() {
        return new UserController();
    }
}
