package com.george.config;

import com.george.filter.AuthFilterCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * <p></p>
 *
 * @author George
 * @date 2024.06.01 12:13
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthFilterCustom authFilterCustom;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
//                .antMatchers("/r/r1").hasAuthority("p2")
//                .antMatchers("/r/r2").hasAuthority("p2")
                .antMatchers("/**").authenticated()//所有的r/**请求必须认证通过
                .anyRequest().permitAll() //其它所有请求都可以随意访问
                .and()
                .addFilterAfter(authFilterCustom, BasicAuthenticationFilter.class) // 添加自定义过滤器
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 关闭session功能，否则会出现上下文缓存问题
    }
}
