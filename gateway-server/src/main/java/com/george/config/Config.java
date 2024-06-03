package com.george.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * <p>
 *     设置跨域资源共享
 *
 *     CorsWebFilter 是Spring WebFlux中的一个过滤器，用于处理CORS请求
 * </p>
 *
 * @author George
 * @date 2024.06.03 19:02
 */
@Configuration
public class Config {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(Boolean.TRUE); // 允许客户端发送包含认证信息的请求（如cookies）。Boolean.TRUE 表示启用该选项
        config.addAllowedMethod("*"); // 允许所有HTTP方法（如GET, POST, PUT, DELETE等）
        config.addAllowedHeader("*"); // 允许所有的请求头
        config.addAllowedOrigin("*"); // 允许所有的源进行跨域请求。这里使用了通配符 *，表示允许来自任何域的请求

        // 创建一个 UrlBasedCorsConfigurationSource 对象，并使用 PathPatternParser 进行路径匹配。
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        // 将上述配置应用于所有路径（/** 表示所有路径）。
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
