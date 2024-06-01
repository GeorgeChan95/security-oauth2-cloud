package com.george.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author George
 */
@Configuration
@MapperScan("com.george.mapper")
public class MybatisPlusConfig {

}
