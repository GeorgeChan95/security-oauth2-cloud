package com.george.intercepter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.dto.UserDetailsExpand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p>
 *     请求拦截器，将用户的token信息放入到一个安全的线程环境下
 * </p>
 *
 * @author George
 * @date 2024.06.03 15:57
 */
@Slf4j
@Component
public class AuthContextIntercepter implements HandlerInterceptor {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 请求执行前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.isNull(authentication) || Objects.isNull(authentication.getPrincipal())){
            //无上下文信息，直接放行
            return true;
        }
        UserDetailsExpand principal = (UserDetailsExpand) authentication.getPrincipal();
        AuthContextHolder.getInstance().setContext(principal);
        return true;
    }

    /**
     * 请求执行后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除线程的用户token信息
        AuthContextHolder.getInstance().clear();
    }
}
