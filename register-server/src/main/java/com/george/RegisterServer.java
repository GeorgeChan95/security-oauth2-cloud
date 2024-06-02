package com.george;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author George Chan
 * @date 2024/6/2 14:06
 * <p></p>
 */
@SpringBootApplication
@EnableEurekaServer
public class RegisterServer {
    public static void main(String[] args) {
        SpringApplication.run(RegisterServer.class, args);
    }
}
