package com.george.controller;

import com.george.dto.UserDetailsExpand;
import com.george.intercepter.AuthContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kdyzm
 */
@RestController
@Slf4j
public class OrderController {

    @GetMapping("/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public UserDetailsExpand r1(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsExpand userDetails = (UserDetailsExpand) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 使用 TransmittableThreadLocal 安全的获取用户token信息
        UserDetailsExpand userDetails = AuthContextHolder.getInstance().getContext();
        log.info("当前接口操作用户，用户名: {}", userDetails.getUsername());
        return userDetails;
    }
}
