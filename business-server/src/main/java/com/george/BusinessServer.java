package com.george;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author George
 */
@SpringBootApplication
@Slf4j
public class BusinessServer {

    public static void main(String[] args) {
        SpringApplication.run(BusinessServer.class, args);
        log.info("服务启动成功，请访问 http://127.0.0.1:30002/index.html 进行测试");
    }
}
