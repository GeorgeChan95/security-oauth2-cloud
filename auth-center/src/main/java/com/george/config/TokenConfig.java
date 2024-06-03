package com.george.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author George
 */
@Configuration
public class TokenConfig {

    private static final String SIGNING_KEY = "auth123";

    @Bean
    public TokenStore tokenStore() {
        // 一个TokenStore的实现类，专门用于处理JWT令牌
        return new JwtTokenStore(accessTokenConverter());
    }

    @Autowired
    private UserDetailsService userDetailsService;
    /**
     * 定义jwt token 对象转换器
     * 这个bean何时被调用，参考：https://blog.csdn.net/qq_42861526/article/details/126172290
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        // 用于转换JWT令牌的类，负责将OAuth2访问令牌转换为JWT格式，并且也负责将JWT令牌转换回OAuth2访问令牌
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // 转换和解析OAuth2访问令牌
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        // 处理用户身份信息的转换
        DefaultUserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();
        // 设置 userDetailsService，用于加载用户的详细信息, 这里的实现类：com.george.service.MyUserDetailsServiceImpl
        userTokenConverter.setUserDetailsService(userDetailsService);
        // 将 userTokenConverter 设置为 tokenConverter 的用户身份信息转换器。
        tokenConverter.setUserTokenConverter(userTokenConverter);
        // 将配置好的 tokenConverter 设置为 jwtAccessTokenConverter 的访问令牌转换器
        jwtAccessTokenConverter.setAccessTokenConverter(tokenConverter);
        // 设置签名密钥。这个密钥用于签名生成的JWT令牌，资源服务器会使用相同的密钥来验证令牌的签名是否有效。
        jwtAccessTokenConverter.setSigningKey(SIGNING_KEY);
        return jwtAccessTokenConverter;
    }
}
