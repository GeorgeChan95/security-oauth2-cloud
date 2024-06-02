package com.george.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author George
 */
@Configuration
@Import({com.george.config.ApiWebMvcConfig.class})
public class Config {
}
