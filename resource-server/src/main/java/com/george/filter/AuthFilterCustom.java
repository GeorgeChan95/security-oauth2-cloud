package com.george.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.models.JwtTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *     自定义过滤器，实现OncePerRequestFilter接口，该过滤器的作用就是获取网关传过来的token-info明文数据，
 *     封装成JwtTokenInfo对象，并将该相关信息添加到SpringSecurity上下文以备之后的鉴权使用
 * </p>
 * @author George Chan
 * @date 2024/6/2 15:50

 */
@Slf4j
@Component
public class AuthFilterCustom extends OncePerRequestFilter { // 继承了OncePerRequestFilter类，确保过滤器每个请求只执行一次
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenInfo=request.getHeader("token-info");
        if(StringUtils.isEmpty(tokenInfo)){
            log.info("未找到token信息");
            filterChain.doFilter(request,response);
            return;
        }
        JwtTokenInfo jwtTokenInfo = objectMapper.readValue(tokenInfo, JwtTokenInfo.class);
        log.info("tokenInfo={}",objectMapper.writeValueAsString(jwtTokenInfo));
        // 获取用户角色拥有的权限的范围
        List<String> authorities = jwtTokenInfo.getAuthorities();
        String[] authoritiesArr = new String[authorities.size()];
        authorities.toArray(authoritiesArr);
        // 将用户信息和权限填充 到用户身份token对象中(设置了用户名、密码null、权限信息)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtTokenInfo.getUser_name(), null, AuthorityUtils.createAuthorityList(authoritiesArr));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //将authenticationToken填充到安全上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
