package com.george.config;

import com.george.handler.MyAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * web安全配置
 * 置安全拦截机制、自定义登录页面、登录失败拦截器
 *
 * @author Administrator
 * @version 1.0
 **/
@Configuration
// 启用全局方法安全性。其中，securedEnabled = true 启用 @Secured 注解，prePostEnabled = true 启用 @PreAuthorize 和 @PostAuthorize 注解。
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 自定义的身份验证失败处理程序
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    /**
     * 认证管理器
     * 提供了身份验证管理器的 bean，以便在 OAuth2 认证流程中使用
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 个密码编码器，用于对用户密码进行加密和验证
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全拦截机制（最重要）
     * 配置 Spring Security 的 HTTP 安全性设置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 禁用了 CSRF（跨站请求伪造）保护。
                .authorizeRequests()
                .antMatchers("/login*","/css/*").permitAll()
                .anyRequest().authenticated() // 其他所有请求都需要进行身份验证
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login") // 指定登录请求URL, 这个请求不必真实存在，浏览器访问，状态是302
                .failureHandler(myAuthenticationFailureHandler);
    }
}
