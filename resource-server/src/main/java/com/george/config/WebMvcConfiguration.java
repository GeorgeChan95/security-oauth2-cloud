package com.george.config;

import com.george.intercepter.AuthContextIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 *     注册自定义拦截器
 * </p>
 *
 * @author George
 * @date 2024.06.03 16:05
 */
@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthContextIntercepter authContextIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加到拦截器注册表中。这个拦截器将在每个HTTP请求到达控制器之前被调用。
        registry.addInterceptor(authContextIntercepter);
    }
}
